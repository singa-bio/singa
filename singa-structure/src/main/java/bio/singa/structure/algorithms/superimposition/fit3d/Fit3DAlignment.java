package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Pair;
import bio.singa.features.identifiers.ECNumber;
import bio.singa.features.identifiers.PfamIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.fit3d.statistics.FofanovEstimation;
import bio.singa.structure.algorithms.superimposition.fit3d.statistics.StatisticalModel;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.sifts.PDBEnzymeMapper;
import bio.singa.structure.parser.sifts.PDBPfamMapper;
import bio.singa.structure.parser.sifts.PDBUniProtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

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
    private final double squaredFilterThreshold;
    private Map<Integer, List<Double>> pairwiseQueryMotifDistanceMap;
    private LabeledSymmetricMatrix<LeafSubstructure> queryMotifSquaredDistanceMatrix;
    private double squaredQueryExtent;
    private LabeledSymmetricMatrix<LeafSubstructure> squaredDistanceMatrix;
    private List<List<LeafSubstructure>> environments;
    private HashMap<List<LeafSubstructure>, List<List<LeafSubstructure>>> candidates;
    private List<Fit3DMatch> matches;

    Fit3DAlignment(Fit3DBuilder.Builder builder) {
        // obtain copies of the input structures
        queryMotif = builder.queryMotif.getCopy();
        target = builder.target.getCopy();
        rmsdCutoff = builder.rmsdCutoff;
        // use squared distance tolerance
        squaredDistanceTolerance = builder.distanceTolerance * builder.distanceTolerance;
        // use squared filter threshold if environment filtering is enabled
        squaredFilterThreshold = builder.filterThreshold * builder.filterThreshold;
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
            logger.debug("environment filtering is enabled with filter threshold of {}", builder.filterThreshold);
            pairwiseQueryMotifDistanceMap = new HashMap<>();
            for (int i = 0; i < queryMotif.getAllLeafSubstructures().size(); i++) {
                for (int j = i + 1; j < queryMotif.getAllLeafSubstructures().size(); j++) {
                    LeafSubstructure firstLeafSubstructure = queryMotif.getAllLeafSubstructures().get(j);
                    LeafSubstructure secondLeafSubstructure = queryMotif.getAllLeafSubstructures().get(i);

                    List<StructuralFamily> firstFamilies = new ArrayList<>();
                    firstFamilies.add(firstLeafSubstructure.getFamily());
                    firstFamilies.addAll(queryMotif.getExchangeableFamilies(firstLeafSubstructure));

                    List<StructuralFamily> secondFamilies = new ArrayList<>();
                    secondFamilies.add(secondLeafSubstructure.getFamily());
                    secondFamilies.addAll(queryMotif.getExchangeableFamilies(secondLeafSubstructure));

                    for (StructuralFamily firstFamily : firstFamilies) {
                        for (StructuralFamily secondFamily : secondFamilies) {
                            int hashCode = generateLabelHashCode(firstFamily, secondFamily);
                            double distance = queryMotifSquaredDistanceMatrix.getValueForLabel(firstLeafSubstructure, secondLeafSubstructure);
                            if (pairwiseQueryMotifDistanceMap.containsKey(hashCode)) {
                                pairwiseQueryMotifDistanceMap.get(hashCode).add(distance);
                            } else {
                                List<Double> distances = new ArrayList<>();
                                distances.add(distance);
                                pairwiseQueryMotifDistanceMap.put(hashCode, distances);
                            }
                        }
                    }
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

    public static int generateLabelHashCode(StructuralFamily structuralFamily1, StructuralFamily structuralFamily2) {
        return structuralFamily1.getThreeLetterCode().hashCode() * structuralFamily2.getThreeLetterCode().hashCode();
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
            matches.stream()
                    // filter hollow matches which are used for p-value calculation
                    .filter(match -> match.getSubstructureSuperimposition() != null)
                    .forEach(match -> {
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
            // annotate title if original target was a structure
            if (target instanceof Structure) {
                matches.forEach(match -> match.setStructureTitle(((Structure) target).getTitle()));
            }
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
     * @param candidate the list of {@link LeafSubstructure} candidates for which alignments should be computed.
     */
    private void computeAlignments(List<LeafSubstructure> candidate) {
        // check if the match is redundant at residue level
        boolean redundant = matches.stream()
                .filter(match -> match.getSubstructureSuperimposition() != null)
                .anyMatch(match -> match.getSubstructureSuperimposition().getCandidate().containsAll(candidate));
        if (redundant) {
            logger.trace("redundant candidate {} skipped", candidate);
            return;
        }
        // apply representation scheme if defined
        SubstructureSuperimposition superimposition;
        if (representationScheme != null) {
            superimposition = SubstructureSuperimposer
                    .calculateSubstructureSuperimposition(queryMotif.getAllLeafSubstructures(),
                            candidate, representationScheme);
        } else {
            superimposition = SubstructureSuperimposer
                    .calculateSubstructureSuperimposition(queryMotif.getAllLeafSubstructures(),
                            candidate, atomFilter);
        }
        if (superimposition.getRmsd() <= rmsdCutoff) {
            // decide if match RMSD is beyond statistical model correctness cutoff
            if (statisticalModel instanceof FofanovEstimation) {
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

    /**
     * Generates all candidates based on the pre-computed environments.
     */
    private void generateCandidates() {
        for (List<LeafSubstructure> environment : environments) {
            ValidCandidateGenerator validCandidateGenerator;
            if (filterEnvironments) {
                validCandidateGenerator = new ValidCandidateGenerator(queryMotif, environment, pairwiseQueryMotifDistanceMap, squaredDistanceMatrix, squaredFilterThreshold);
            } else {
                validCandidateGenerator = new ValidCandidateGenerator(queryMotif, environment);
            }
            List<List<LeafSubstructure>> currentCandidates = validCandidateGenerator.getCandidates();
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
            List<LeafSubstructure> environment = new ArrayList<>();
            for (int i = 0; i < distanceToOthers.getElements().length; i++) {
                double currentDistance = distanceToOthers.getElement(i);
                if (currentDistance <= squaredQueryExtent + squaredDistanceTolerance) {
                    environment.add(squaredDistanceMatrix.getColumnLabel(i));
                }
            }
            if (environment.size() >= queryMotif.size()) {
                logger.debug("possible environment {} within around {} added", environment, currentSubstructure);
                environments.add(environment);
            }
        }
    }
}