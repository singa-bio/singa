package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.scoring.XieScore;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.model.oak.Structures;
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
    private double squaredQueryExtent;
    private LabeledSymmetricMatrix<LeafSubstructure<?>> squaredDistanceMatrix;
    private List<List<LeafSubstructure<?>>> environments;
    private HashMap<List<LeafSubstructure<?>>, Set<Set<LeafSubstructure<?>>>> candidates;
    private double rmsdCutoff;
    private TreeMap<Double, SubstructureSuperimposition> matches;
    private Predicate<Atom> atomFilter;

    Fit3DAlignment(Fit3DBuilder.Builder builder) {
        // obtain copies of the input structures
        this.queryMotif = builder.queryMotif.getCopy();
        this.target = builder.target.getCopy();
        this.rmsdCutoff = builder.rmsdCutoff;
        // use squared distance tolerance
        this.squaredDistanceTolerance = builder.distanceTolerance * builder.distanceTolerance;
        this.atomFilter = builder.atomFilter;
        this.representationScheme = builder.representationScheme;

        if (this.queryMotif.size() > this.target.getAllLeafSubstructures().size()) {
            throw new Fit3DException("search target " + this.target + " must contain at least as many atom-containing substructures " +
                    "as the query motif");
        }

        // initialize
        this.environments = new ArrayList<>();
        this.matches = new TreeMap<>();
        this.candidates = new HashMap<>();

        logger.debug("computing Fit3D alignment of motif {} against {}", this.queryMotif, this.target);

        // reduce target structures to the types that are actually occurring in the query motif or defined exchanges
        reduceTargetStructure();
        if (this.queryMotif.size() > this.target.getAllLeafSubstructures().size()) {
            logger.debug("reduced target structure smaller than query motif, no matches can be found");
            return;
        }
        // calculate squared motif extent
        calculateMotifExtent();

        // calculate squared distance matrix
        this.squaredDistanceMatrix = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(this.target.getAllLeafSubstructures(), LeafSubstructure::getPosition);
        logger.debug("the target structure squared distance matrix is\n{}",
                this.squaredDistanceMatrix.getStringRepresentation());

        composeEnvironments();
        generateCandidates();
        computeMatches();
    }

    /**
     * Returns the computed matches of this Fit3D search.
     *
     * @return The matches of this search.
     */
    @Override
    public TreeMap<Double, SubstructureSuperimposition> getMatches() {
        return this.matches;
    }

    /**
     * Returns the fraction of aligned residues, which is always 1.0 for this kind of alignment.
     */
    @Override
    public double getFraction() {
        return 1.0;
    }

    @Override
    public XieScore getXieScore() {
        throw new UnsupportedOperationException("Xie score can only be calculate for Fit3DSiteAlignment");
    }

    /**
     * Computes all matches of the generated candidates.
     */
    private void computeMatches() {
        this.candidates.values().stream()
                .flatMap(Collection::stream)
                .forEach(this::computeAlignments);
    }

    /**
     * Computes all valid alignments of a given {@link LeafSubstructure}.
     *
     * @param leafSubstructures the {@link LeafSubstructure} for which alignments should be computed.
     */
    private void computeAlignments(Set<LeafSubstructure<?>> leafSubstructures) {
        ValidAlignmentGenerator validAlignmentGenerator =
                new ValidAlignmentGenerator(this.queryMotif.getAllLeafSubstructures(), new ArrayList<>(leafSubstructures));
        List<List<Pair<LeafSubstructure<?>>>> validAlignments = validAlignmentGenerator.getValidAlignments();
        for (List<Pair<LeafSubstructure<?>>> validAlignment : validAlignments) {
            // create candidate for alignment
            List<LeafSubstructure<?>> alignmentCandidate = validAlignment.stream()
                    .map(Pair::getSecond).collect(Collectors.toList());
            // apply representation scheme if defined
            SubstructureSuperimposition superimposition;
            if (this.representationScheme != null) {
                superimposition = SubstructureSuperimposer
                        .calculateSubstructureSuperimposition(this.queryMotif.getAllLeafSubstructures(),
                                alignmentCandidate, this.representationScheme);
            } else {
                superimposition = SubstructureSuperimposer
                        .calculateSubstructureSuperimposition(this.queryMotif.getAllLeafSubstructures(),
                                alignmentCandidate, this.atomFilter);
            }
            if (superimposition.getRmsd() <= this.rmsdCutoff) {
                this.matches.put(superimposition.getRmsd(), superimposition);
            }
        }
    }

    /**
     * Generates all candidates based on the pre-computed environments.
     */
    private void generateCandidates() {
        for (List<LeafSubstructure<?>> environment : this.environments) {
            Set<Set<LeafSubstructure<?>>> currentCandidates = new ValidCandidateGenerator(
                    this.queryMotif.getAllLeafSubstructures(),
                    environment).getValidCandidates();
            this.candidates.put(environment, currentCandidates);
        }
    }

    public List<List<LeafSubstructure<?>>> getEnvironments() {
        return this.environments;
    }

    /**
     * Determines the maximal spatial extent of the query motif, measured on the centroid of all atoms.
     */
    private void calculateMotifExtent() {
        LabeledSymmetricMatrix<LeafSubstructure<?>> queryDistanceMatrix =
                Structures.calculateSquaredDistanceMatrix(this.queryMotif);
        // position of maximal element is always symmetric, hence we consider the first
        Pair<Integer> positionOfMaximalElement = Matrices.getPositionsOfMaximalElement(queryDistanceMatrix).stream()
                .findFirst()
                .orElseThrow(() -> new Fit3DException("could not determine extent of the query motif"));
        this.squaredQueryExtent = queryDistanceMatrix.getElement(positionOfMaximalElement.getFirst(),
                positionOfMaximalElement.getSecond());
        logger.debug("the squared query motif extent is {}", this.squaredQueryExtent);
    }


    /**
     * Reduces the target structure only to the {@link StructuralFamily} types that are contained in the query motif or
     * its defined exchanges.
     */
    private void reduceTargetStructure() {
        // collect all containing types (own types <b>plus</b> exchangeable types) of the query motif
        Set<Object> containingTypes = this.queryMotif.getAllLeafSubstructures().stream()
                .map(LeafSubstructure::getContainingFamilies)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<LeafSubstructure> toBeRemoved = this.target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> !containingTypes.contains(leafSubstructure.getFamily()))
                .collect(Collectors.toList());
        toBeRemoved.forEach(this.target::removeLeafSubstructure);
    }

    /**
     * Creates all micro-environments that can be built by iterating over the backbone.
     */
    private void composeEnvironments() {
        // iterate over reduced target structure
        for (LeafSubstructure currentSubstructure : this.target.getAllLeafSubstructures()) {
            // collect environments within the bounds if the motif extent
            RegularVector distanceToOthers = this.squaredDistanceMatrix.getColumnByLabel(currentSubstructure);
            List<LeafSubstructure<?>> environment = new ArrayList<>();
            for (int i = 0; i < distanceToOthers.getElements().length; i++) {
                double currentDistance = distanceToOthers.getElement(i);
                if (currentDistance <= this.squaredQueryExtent + this.squaredDistanceTolerance) {
                    environment.add(this.squaredDistanceMatrix.getColumnLabel(i));
                }
            }
            if (environment.size() >= this.queryMotif.size()) {
                logger.debug("possible environment {} within {} around {} added", environment,
                        Math.sqrt(this.squaredQueryExtent + this.squaredDistanceTolerance), currentSubstructure);
                this.environments.add(environment);
            }
        }
    }
}