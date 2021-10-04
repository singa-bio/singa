package bio.singa.structure.algorithms.superimposition.affinity;


import bio.singa.mathematics.algorithms.clustering.AffinityPropagation;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.structure.algorithms.superimposition.AlignmentMethod;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.algorithms.superimposition.consensus.ConsensusAlignment;
import bio.singa.structure.algorithms.superimposition.consensus.ConsensusBuilder;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.general.StructuralEntityFilter;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An affinity alignment of same-sized {@link StructuralMotif}s can be used to cluster them according their geometric
 * similarity in a multi-structure alignment manner. <b>NOTE:</b> Copies of given {@link StructuralMotif}s will be used,
 * so original structures are not altered.
 *
 * @author fk
 */
public class AffinityAlignment extends AlignmentMethod {

    private static final Logger logger = LoggerFactory.getLogger(AffinityAlignment.class);
    private static final int MAXIMAL_EPOCHS = 1000;
    private static final double LAMBDA = 0.5;

    private final List<StructuralMotif> input;

    private LabeledSymmetricMatrix<StructuralMotif> distanceMatrix;
    private double selfDissimilarity;
    private Map<StructuralMotif, List<StructuralMotif>> clusters;
    private double silhouetteCoefficient;

    private AffinityAlignment(Builder builder) {

        // get copy of input structural motifs
        input = builder.structuralMotifs.stream()
                .map(StructuralMotif::getCopy)
                .collect(Collectors.toList());

        setAtomFilter(builder.atomFilter);

        // create representation scheme if given
        setRepresentationSchemeFromType(builder.representationSchemeType);

        setIdealSuperimposition(builder.idealSuperimposition);

        logger.info("affinity alignment initialized with {} input structures", input.size());

        // check if all substructures are of the same size
        if (input.stream()
                .map(StructuralMotif::getAllLeafSubstructures)
                .map(List::size)
                .collect(Collectors.toSet()).size() != 1) {
            throw new IllegalArgumentException("all substructures must contain the same number of leaf structures to " +
                    "calculate a consensus alignment");
        }

        // calculate initial alignments
        calculateInitialAlignments();
        computeClustering();
        // align within clusters if specified
        if (builder.alignWithinClusters) {
            alignWithinClusters();
        }
    }

    public static InputStep create() {
        return new Builder();
    }

    public LabeledSymmetricMatrix<StructuralMotif> getDistanceMatrix() {
        return distanceMatrix;
    }

    public double getSelfDissimilarity() {
        return selfDissimilarity;
    }

    public double getSilhouetteCoefficient() {
        return silhouetteCoefficient;
    }

    public Map<StructuralMotif, List<StructuralMotif>> getClusters() {
        return clusters;
    }

    private void alignWithinClusters() {
        StructuralMotif reference;
        for (Map.Entry<StructuralMotif, List<StructuralMotif>> entry : clusters.entrySet()) {
            List<StructuralMotif> alignedCluster = new ArrayList<>();
            reference = entry.getKey();
            for (StructuralMotif structuralMotif : entry.getValue()) {
                SubstructureSuperimposition superimposition = superimpose(reference, structuralMotif);
                alignedCluster.add(StructuralMotif.fromLeafSubstructures(superimposition.getMappedFullCandidate()));
            }
            entry.setValue(alignedCluster);
        }
    }

    private void computeClustering() {
        AffinityPropagation<StructuralMotif> affinityPropagation = AffinityPropagation.<StructuralMotif>create()
                .dataPoints(input)
                .matrix(distanceMatrix)
                .isDistance(true)
                .selfSimilarityByMedian()
                .maximalEpochs(MAXIMAL_EPOCHS)
                .lambda(LAMBDA)
                .run();
        silhouetteCoefficient = affinityPropagation.getSilhouetteCoefficient();
        clusters = affinityPropagation.getClusters();
        logger.info("found {} clusters", clusters.size());
    }

    private void calculateInitialAlignments() {
        double[][] temporaryDistanceMatrix = new double[input.size()][input.size()];
        for (int i = 0; i < input.size() - 1; i++) {
            for (int j = i + 1; j < input.size(); j++) {

                StructuralMotif reference = input.get(i);
                StructuralMotif candidate = input.get(j);

                // calculate superimposition
                SubstructureSuperimposition superimposition = superimpose(reference, candidate);

                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();
            }
        }
        distanceMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        distanceMatrix.setRowLabels(input);
    }

    /**
     * Writes the created clusters to the specified {@link Path}.
     *
     * @param outputPath the desired output {@link Path}
     * @throws IOException If the path cannot be written to.
     */
    public void writeClusters(Path outputPath) throws IOException {
        logger.info("writing {} clusters to {}", clusters.size(), outputPath);
        Files.createDirectories(outputPath);
        int clusterCounter = 0;
        for (Map.Entry<StructuralMotif, List<StructuralMotif>> entry : clusters.entrySet()) {
            String clusterBaseLocation = "cluster_" + (clusterCounter + 1) + "/";
            // write exemplar
            StructureWriter.pdb()
                    .substructures(entry.getKey().getAllLeafSubstructures())
                    .writeToPath(outputPath.resolve(clusterBaseLocation + "exemplar_" + (clusterCounter + 1) + "_" + entry.getKey() + ".pdb"));

            // write cluster members
            for (StructuralMotif structuralMotif : entry.getValue()) {
                StructureWriter.pdb()
                        .substructures(structuralMotif.getAllLeafSubstructures())
                        .writeToPath(outputPath.resolve(clusterBaseLocation + structuralMotif + ".pdb"));
            }
            clusterCounter++;
        }
    }

    public interface InputStep {

        AtomStep inputStructuralMotifs(List<StructuralMotif> structuralMotifs);
    }

    public interface ParameterStep {

        ParameterStep idealSuperimposition(boolean idealSuperimposition);

        ParameterStep alignWithinClusters(boolean alignWithinClusters);

        /**
         * Creates a new {@link ConsensusAlignment} and starts the calculation.
         *
         * @return A new {@link ConsensusAlignment} once calculation has finished.
         */
        AffinityAlignment run();
    }

    public interface AtomStep {
        /**
         * Defines a {@link RepresentationSchemeType} to be used for the computed alignments. This is exclusive to the
         * definition of an {@link StructuralEntityFilter.AtomFilter}.
         *
         * @param representationSchemeType The {@link RepresentationSchemeType} to be used.
         * @return The {@link ConsensusBuilder.ParameterStep} to define additional parameters.
         */
        ParameterStep representationSchemeType(RepresentationSchemeType representationSchemeType);

        /**
         * Defines the {@link StructuralEntityFilter.AtomFilter} to be used for the computed alignments. This is
         * exclusive to the definition of a {@link RepresentationSchemeType}.
         *
         * @param atomFilter The {@link StructuralEntityFilter.AtomFilter} to be used.
         * @return The {@link ConsensusBuilder.ParameterStep} to define additional parameters.
         */
        ParameterStep atomFilter(Predicate<Atom> atomFilter);

        /**
         * Creates a new {@link ConsensusAlignment} and starts the calculation.
         *
         * @return A new {@link ConsensusAlignment} once calculation has finished.
         */
        AffinityAlignment run();
    }

    public static class Builder implements InputStep, AtomStep, ParameterStep {

        private static final boolean DEFAULT_ALIGN_WITHIN_CLUSTERS = true;
        private static final Predicate<Atom> DEFAULT_ATOM_FILTER = StructuralEntityFilter.AtomFilter.isArbitrary();
        private static final RepresentationSchemeType DEFAULT_REPRESENTATION_SCHEME_TYPE = null;
        private static final boolean DEFAULT_IDEAL_SUPERIMPOSITION = false;

        RepresentationSchemeType representationSchemeType = DEFAULT_REPRESENTATION_SCHEME_TYPE;
        Predicate<Atom> atomFilter = DEFAULT_ATOM_FILTER;
        boolean idealSuperimposition = DEFAULT_IDEAL_SUPERIMPOSITION;
        boolean alignWithinClusters = DEFAULT_ALIGN_WITHIN_CLUSTERS;
        private List<StructuralMotif> structuralMotifs;

        private Builder() {

        }

        @Override
        public AtomStep inputStructuralMotifs(List<StructuralMotif> structuralMotifs) {
            this.structuralMotifs = structuralMotifs;
            return this;
        }

        @Override
        public ParameterStep representationSchemeType(RepresentationSchemeType representationSchemeType) {
            this.representationSchemeType = representationSchemeType;
            return this;
        }

        @Override
        public ParameterStep atomFilter(Predicate<Atom> atomFilter) {
            this.atomFilter = atomFilter;
            return this;
        }

        @Override
        public AffinityAlignment run() {
            return new AffinityAlignment(this);
        }

        @Override
        public ParameterStep idealSuperimposition(boolean idealSuperimposition) {
            this.idealSuperimposition = idealSuperimposition;
            return this;
        }

        @Override
        public ParameterStep alignWithinClusters(boolean alignWithinClusters) {
            this.alignWithinClusters = alignWithinClusters;
            return this;
        }
    }
}
