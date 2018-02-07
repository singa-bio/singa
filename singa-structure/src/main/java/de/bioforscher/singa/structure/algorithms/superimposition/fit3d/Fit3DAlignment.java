package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.identifier.ECNumber;
import de.bioforscher.singa.core.identifier.PfamIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.core.utility.CommutablePair;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.statistics.FofanovEstimation;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.statistics.StatisticalModel;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.sifts.PDBEnzymeMapper;
import de.bioforscher.singa.structure.parser.sifts.PDBPfamMapper;
import de.bioforscher.singa.structure.parser.sifts.PDBUniProtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * An implementation of the Fit3D algorithm for substructure search.
 *
 * @author fk
 */
public class Fit3DAlignment implements Fit3D {

    private static final Logger logger = LoggerFactory.getLogger(Fit3DAlignment.class);

    private final StructuralMotif queryMotif;
    private final LeafSubstructureContainer target;
    private final double squaredDistanceTolerance;
    private final RepresentationScheme representationScheme;
    private final StatisticalModel statisticalModel;
    private final double rmsdCutoff;
    private final Predicate<Atom> atomFilter;
    private final boolean mapEcNumbers;
    private final boolean mapPfamIdentifiers;
    private final boolean mapUniProtIdentifiers;
    private final boolean filterEnvironments;
    private LabeledSymmetricMatrix<LeafSubstructure<?>> queryMotifSquaredDistanceMatrix;
    private double squaredQueryExtent;
    private List<CommutablePair<LeafSubstructure<?>>> queryMotifPairs;
    private LabeledSymmetricMatrix<LeafSubstructure<?>> squaredDistanceMatrix;
    private List<List<LeafSubstructure<?>>> environments;
    private HashMap<List<LeafSubstructure<?>>, Set<Set<LeafSubstructure<?>>>> candidates;
    private List<Fit3DMatch> matches;

    Fit3DAlignment(Fit3DBuilder.Builder builder) {
        // obtain copies of the input structures
        queryMotif = builder.queryMotif.getCopy();
        target = builder.target.getCopy();
        rmsdCutoff = builder.rmsdCutoff;
        // use squared distance tolerance
        squaredDistanceTolerance = builder.distanceTolerance * builder.distanceTolerance;
        atomFilter = builder.atomFilter;
        representationScheme = builder.representationScheme;
        statisticalModel = builder.statisticalModel;
        mapUniProtIdentifiers = builder.mapUniprotIdentifiers;
        mapPfamIdentifiers = builder.mapPfamIdentifiers;
        mapEcNumbers = builder.mapEcNumbers;
        filterEnvironments = builder.filterEnvironments;

        if (queryMotif.size() > target.getNumberOfLeafSubstructures()) {
            throw new Fit3DException("search target " + target + " must contain at least as many atom-containing substructures " +
                    "as the query motif");
        }

        // initialize
        environments = new ArrayList<>();
        matches = new ArrayList<>();
        candidates = new HashMap<>();

        logger.debug("computing Fit3D alignment of motif {} against {}", queryMotif, target);

        // reduce target structures to the types that are actually occurring in the query motif or defined exchanges
        target.removeLeafSubstructuresNotRelevantFor(queryMotif);
        if (queryMotif.size() > target.getNumberOfLeafSubstructures()) {
            logger.debug("reduced target structure smaller than query motif, no matches can be found");
            return;
        }
        // calculate squared motif extent
        queryMotifSquaredDistanceMatrix = VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(queryMotif.getAllLeafSubstructures(), LeafSubstructure::getPosition);
        Pair<Integer> positionOfMaximalElement = Matrices.getPositionsOfMaximalElement(queryMotifSquaredDistanceMatrix).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("could not determine the maximal squared extent of " + queryMotif));
        squaredQueryExtent = queryMotifSquaredDistanceMatrix.getElement(positionOfMaximalElement.getFirst(),
                positionOfMaximalElement.getSecond());
        logger.debug("the squared query motif extent is {}", squaredQueryExtent);

        if (filterEnvironments) {
            // generate query motif pairs
            queryMotifPairs = new ArrayList<>();
            for (int i = 0; i < queryMotif.getAllLeafSubstructures().size(); i++) {
                for (int j = i + 1; j < queryMotif.getAllLeafSubstructures().size(); j++) {
                    CommutablePair<LeafSubstructure<?>> queryMotifPair = new CommutablePair<>(queryMotif.getAllLeafSubstructures().get(i),
                            queryMotif.getAllLeafSubstructures().get(j));
                    queryMotifPairs.add(queryMotifPair);
                }
            }
        }

        // calculate squared distance matrix
        squaredDistanceMatrix = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(target.getAllLeafSubstructures(), LeafSubstructure::getPosition);

        composeEnvironments();
        generateCandidates();
        computeMatches();
        calculateStatistics();
        mapIdentifiers();
    }

    /**
     * Calculates statistics for the given {@link StatisticalModel} if any.
     */
    private void calculateStatistics() {
        if (statisticalModel != null) {
            // if Fofanov statistical model is used
            if (statisticalModel instanceof FofanovEstimation) {
                if (!matches.isEmpty()) {
                    // increase GS if matches were found
                    logger.debug("match found, increasing GS count for Fofanov model");
                    ((FofanovEstimation) statisticalModel).incrementGs();
                } else if (!candidates.isEmpty()) {
                    // increase NS if matches were not found but could have been found
                    logger.debug("no match found, but theoretically possible, increasing NS");
                    ((FofanovEstimation) statisticalModel).incrementNs();
                }
            }
        }
    }

    /**
     * Maps diverse identifiers if specified.
     */
    private void mapIdentifiers() {
        if (mapUniProtIdentifiers || mapPfamIdentifiers || mapEcNumbers) {
            logger.debug("mapping identifiers for matches: UniProt: {}, Pfam: {}, EC: {}", mapUniProtIdentifiers, mapPfamIdentifiers, mapEcNumbers);
            matches.forEach(match -> {
                String pdbIdentifier = match.getSubstructureSuperimposition().getCandidate().get(0).getPdbIdentifier();
                List<String> chainIdentifiers = match.getSubstructureSuperimposition().getCandidate().stream()
                        .map(LeafSubstructure::getChainIdentifier)
                        .distinct()
                        .collect(Collectors.toList());
                Map<String, UniProtIdentifier> uniProtIdentifiers;
                if (mapUniProtIdentifiers) {
                    uniProtIdentifiers = PDBUniProtMapper.map(pdbIdentifier);
                    uniProtIdentifiers.keySet().retainAll(chainIdentifiers);
                    if (!uniProtIdentifiers.isEmpty()) {
                        match.setUniProtIdentifiers(uniProtIdentifiers);
                    }
                }
                if (mapPfamIdentifiers) {
                    Map<String, PfamIdentifier> pfamIdentifiers = PDBPfamMapper.map(pdbIdentifier);
                    pfamIdentifiers.keySet().retainAll(chainIdentifiers);
                    if (!pfamIdentifiers.isEmpty()) {
                        match.setPfamIdentifiers(pfamIdentifiers);
                    }
                }
                if (mapEcNumbers) {
                    Map<String, ECNumber> ecNumbers = PDBEnzymeMapper.map(pdbIdentifier);
                    ecNumbers.keySet().retainAll(chainIdentifiers);
                    if (!ecNumbers.isEmpty()) {
                        match.setEcNumbers(ecNumbers);
                    }
                }
            });
        }
    }

    /**
     * Returns the computed matches of this Fit3D search.
     *
     * @return The matches of this search.
     */
    @Override
    public List<Fit3DMatch> getMatches() {
        return matches;
    }

    /**
     * Returns the fraction of aligned residues, which is always 1.0 for this kind of alignment.
     */
    @Override
    public double getFraction() {
        return 1.0;
    }

    /**
     * Computes all matches of the generated candidates.
     */
    private void computeMatches() {
        candidates.values().stream()
                .flatMap(Collection::stream)
                .forEach(this::computeAlignments);
        Collections.sort(matches);
    }

    /**
     * Computes all valid alignments of a given {@link LeafSubstructure}.
     *
     * @param leafSubstructures the {@link LeafSubstructure} for which alignments should be computed.
     */
    private void computeAlignments(Set<LeafSubstructure<?>> leafSubstructures) {
        // check if the match is redundant on residue level
        boolean redundant = matches.stream()
                .filter(match -> match.getSubstructureSuperimposition() != null)
                .anyMatch(match -> match.getSubstructureSuperimposition().getCandidate().containsAll(leafSubstructures));
        if (redundant) {
            logger.trace("redundant candidate {} skipped", leafSubstructures);
            return;
        }
        ValidAlignmentGenerator validAlignmentGenerator =
                new ValidAlignmentGenerator(queryMotif.getAllLeafSubstructures(), new ArrayList<>(leafSubstructures));
        List<List<Pair<LeafSubstructure<?>>>> validAlignments = validAlignmentGenerator.getValidAlignments();
        for (List<Pair<LeafSubstructure<?>>> validAlignment : validAlignments) {
            // create candidate for alignment
            List<LeafSubstructure<?>> alignmentCandidate = validAlignment.stream()
                    .map(Pair::getSecond).collect(Collectors.toList());
            // apply representation scheme if defined
            SubstructureSuperimposition superimposition;
            if (representationScheme != null) {
                superimposition = SubstructureSuperimposer
                        .calculateSubstructureSuperimposition(queryMotif.getAllLeafSubstructures(),
                                alignmentCandidate, representationScheme);
            } else {
                superimposition = SubstructureSuperimposer
                        .calculateSubstructureSuperimposition(queryMotif.getAllLeafSubstructures(),
                                alignmentCandidate, atomFilter);
            }
            if (superimposition.getRmsd() <= rmsdCutoff) {
                // decide if match RMSD is beyond statistical model correctness cutoff
                if (statisticalModel != null && statisticalModel instanceof FofanovEstimation) {
                    if (superimposition.getRmsd() <= ((FofanovEstimation) statisticalModel).getModelCorrectnessCutoff()) {
                        matches.add(Fit3DMatch.of(superimposition.getRmsd(), superimposition));
                    } else {
                        // only store RMSD values if match RMSD is beyond model correctness cutoff and which were not already sampled
                        boolean redundantRmsd = matches.stream()
                                .anyMatch(match -> match.getRmsd() == superimposition.getRmsd());
                        if (!redundantRmsd) {
                            matches.add(Fit3DMatch.of(superimposition.getRmsd()));
                        }
                    }
                } else {
                    matches.add(Fit3DMatch.of(superimposition.getRmsd(), superimposition));
                }
            }
        }
    }

    /**
     * Generates all candidates based on the pre-computed environments.
     */
    private void generateCandidates() {
        for (List<LeafSubstructure<?>> environment : environments) {
            Set<Set<LeafSubstructure<?>>> currentCandidates = new ValidCandidateGenerator(
                    queryMotif.getAllLeafSubstructures(),
                    environment).getValidCandidates();
            if (!currentCandidates.isEmpty()) {
                candidates.put(environment, currentCandidates);
            }
        }
    }

    /**
     * Creates all micro-environments that can be built by iterating over the backbone.
     */
    private void composeEnvironments() {
        // iterate over reduced target structure
        for (LeafSubstructure currentSubstructure : target.getAllLeafSubstructures()) {
            // collect environments within the bounds if the motif extent
            RegularVector distanceToOthers = squaredDistanceMatrix.getColumnByLabel(currentSubstructure);
            List<LeafSubstructure<?>> environment = new ArrayList<>();
            for (int i = 0; i < distanceToOthers.getElements().length; i++) {
                double currentDistance = distanceToOthers.getElement(i);
                if (currentDistance <= squaredQueryExtent + squaredDistanceTolerance) {
                    environment.add(squaredDistanceMatrix.getColumnLabel(i));
                }
            }
            if (environment.size() >= queryMotif.size()) {
                logger.debug("possible environment {} within around {} added", environment, currentSubstructure);
                if (filterEnvironments && filterEnvironment(environment)) {
                    environments.add(environment);
                } else {
                    environments.add(environment);
                }
            }
        }
    }

    private boolean filterEnvironment(List<LeafSubstructure<?>> environment) {
        // generate environment pairs
        List<CommutablePair<LeafSubstructure<?>>> environmentPairs = new ArrayList<>();
        for (int i = 0; i < environment.size(); i++) {
            for (int j = i + 1; j < environment.size(); j++) {
                environmentPairs.add(new CommutablePair<>(environment.get(i), environment.get(j)));
            }
        }
        // check for compatibility of distances
        for (CommutablePair<LeafSubstructure<?>> queryMotifPair : queryMotifPairs) {
            double queryMotifPairDistance = queryMotifSquaredDistanceMatrix.getValueForLabel(queryMotifPair.getFirst(), queryMotifPair.getSecond());
            // determine all compatible pairs contained in the environment
            List<CommutablePair<LeafSubstructure<?>>> compatiblePairs = environmentPairs.stream()
                    .filter(environmentPair -> (queryMotifPair.getFirst().getExchangeableFamilies().contains(environmentPair.getFirst().getFamily()) ||
                            queryMotifPair.getSecond().getExchangeableFamilies().contains(environmentPair.getSecond().getFamily())) ||
                            (queryMotifPair.getFirst().getFamily() == environmentPair.getFirst().getFamily() ||
                                    queryMotifPair.getFirst().getFamily() == environmentPair.getSecond().getFamily())
                    )
                    .filter(environmentPair -> (queryMotifPair.getSecond().getExchangeableFamilies().contains(environmentPair.getSecond().getFamily()) ||
                            queryMotifPair.getSecond().getExchangeableFamilies().contains(environmentPair.getFirst().getFamily())) ||
                            (queryMotifPair.getSecond().getFamily() == environmentPair.getSecond().getFamily() ||
                                    queryMotifPair.getSecond().getFamily() == environmentPair.getFirst().getFamily())
                    )
                    .collect(Collectors.toList());
            boolean hasCompatiblePair = false;
            for (CommutablePair<LeafSubstructure<?>> compatiblePair : compatiblePairs) {
                double compatiblePairDistance = squaredDistanceMatrix.getValueForLabel(compatiblePair.getFirst(), compatiblePair.getSecond());
                if (compatiblePairDistance >= queryMotifPairDistance - squaredDistanceTolerance &&
                        compatiblePairDistance <= queryMotifPairDistance + squaredDistanceTolerance) {
                    hasCompatiblePair = true;
                }
            }
            // immediately return false if one pair of the query motif does not have a valid pair in the environment
            if (!hasCompatiblePair) {
                return false;
            }
        }
        return true;
    }
}