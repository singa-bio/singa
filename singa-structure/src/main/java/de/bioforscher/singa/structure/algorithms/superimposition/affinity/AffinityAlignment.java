package de.bioforscher.singa.structure.algorithms.superimposition.affinity;


import de.bioforscher.singa.mathematics.algorithms.clustering.AffinityPropagation;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.algorithms.superimposition.consensus.ConsensusAlignment;
import de.bioforscher.singa.structure.algorithms.superimposition.consensus.ConsensusBuilder;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureWriter;
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
public class AffinityAlignment {

    private static final Logger logger = LoggerFactory.getLogger(AffinityAlignment.class);
    private static final int MAXIMAL_EPOCHS = 1000;
    private static final double LAMBDA = 0.5;

    private final List<StructuralMotif> input;
    private final boolean idealSuperimposition;
    private final Predicate<Atom> atomFilter;

    private RepresentationScheme representationScheme;
    private LabeledSymmetricMatrix<StructuralMotif> distanceMatrix;
    private double selfDissimilarity;
    private Map<StructuralMotif, List<StructuralMotif>> clusters;
    private double silhouetteCoefficient;

    private AffinityAlignment(Builder builder) {

        // get copy of input structural motifs
        this.input = builder.structuralMotifs.stream()
                .map(StructuralMotif::getCopy)
                .collect(Collectors.toList());

        // create representation scheme if given
        RepresentationSchemeType representationSchemeType = builder.representationSchemeType;
        if (representationSchemeType != null) {
            logger.info("using representation scheme {}", representationSchemeType);
            this.representationScheme = RepresentationSchemeFactory.createRepresentationScheme(representationSchemeType);
        }

        this.idealSuperimposition = builder.idealSuperimposition;
        this.atomFilter = builder.atomFilter;

        logger.info("affinity alignment initialized with {} input structures", this.input.size());

        // check if all substructures are of the same size
        if (this.input.stream()
                .map(StructuralMotif::getAllLeafSubstructures)
                .map(List::size)
                .collect(Collectors.toSet()).size() != 1) {
            throw new IllegalArgumentException("all substructures must contain the same number of leaf structures to " +
                    "calculate a consensus alignment");
        }

        // calculate initial alignments
        calculateInitialAlignments();
        determineSelfSimilarity();
        computeClustering();
        // align within clusters if specified
        if (builder.alignWithinClusters) {
            alignWithinClusters();
        }
    }

    public static InputStep create() {
        return new Builder();
    }

    public double getSelfDissimilarity() {
        return this.selfDissimilarity;
    }

    public double getSilhouetteCoefficient() {
        return this.silhouetteCoefficient;
    }

    public Map<StructuralMotif, List<StructuralMotif>> getClusters() {
        return this.clusters;
    }

    private void alignWithinClusters() {
        StructuralMotif reference;
        for (Map.Entry<StructuralMotif, List<StructuralMotif>> entry : this.clusters.entrySet()) {
            List<StructuralMotif> alignedCluster = new ArrayList<>();
            reference = entry.getKey();
            for (StructuralMotif structuralMotif : entry.getValue()) {
                SubstructureSuperimposition superimposition;
                if (this.representationScheme == null) {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    structuralMotif, this.atomFilter) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getAllLeafSubstructures(),
                                    structuralMotif.getAllLeafSubstructures(), this.atomFilter);
                } else {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    structuralMotif, this.representationScheme) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getAllLeafSubstructures(),
                                    structuralMotif.getAllLeafSubstructures(), this.representationScheme);
                }
                alignedCluster.add(StructuralMotif.fromLeafSubstructures(superimposition.getMappedFullCandidate()));
            }
            entry.setValue(alignedCluster);
        }
    }

    private void determineSelfSimilarity() {
        this.selfDissimilarity = Vectors.getMedian(new RegularVector(this.distanceMatrix.streamElements().toArray()));
        logger.info("self-dissimilarity of input structures (median of RMSD values) is {}", this.selfDissimilarity);
    }

    private void computeClustering() {
        AffinityPropagation<StructuralMotif> affinityPropagation = AffinityPropagation.<StructuralMotif>create()
                .dataPoints(this.input)
                .matrix(this.distanceMatrix)
                .isDistance(true)
                .selfSimilarity(this.selfDissimilarity)
                .maximalEpochs(MAXIMAL_EPOCHS)
                .lambda(LAMBDA)
                .run();
        this.silhouetteCoefficient = affinityPropagation.getSilhouetteCoefficient();
        this.clusters = affinityPropagation.getClusters();
        logger.info("found {} clusters", this.clusters.size());
    }

    private void calculateInitialAlignments() {
        double[][] temporaryDistanceMatrix = new double[this.input.size()][this.input.size()];
        for (int i = 0; i < this.input.size() - 1; i++) {
            for (int j = i + 1; j < this.input.size(); j++) {

                StructuralMotif reference = this.input.get(i);
                StructuralMotif candidate = this.input.get(j);

                // calculate superimposition
                SubstructureSuperimposition superimposition;
                if (this.representationScheme == null) {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    candidate, this.atomFilter) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getAllLeafSubstructures(),
                                    candidate.getAllLeafSubstructures(), this.atomFilter);
                } else {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    candidate, this.representationScheme) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getAllLeafSubstructures(),
                                    candidate.getAllLeafSubstructures(), this.representationScheme);
                }

                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();
            }
        }
        this.distanceMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        this.distanceMatrix.setRowLabels(this.input);
    }

    /**
     * Writes the created clusters to the specified {@link Path}.
     *
     * @param outputPath the desired output {@link Path}
     * @throws IOException If the path cannot be written to.
     */
    public void writeClusters(Path outputPath) throws IOException {
        logger.info("writing {} clusters to {}", this.clusters.size(), outputPath);
        Files.createDirectories(outputPath);
        int clusterCounter = 0;
        for (Map.Entry<StructuralMotif, List<StructuralMotif>> entry : this.clusters.entrySet()) {
            String clusterBaseLocation = "cluster_" + (clusterCounter + 1) + "/";
            List<StructuralMotif> currentCluster = entry.getValue();
            // write exemplar
            StructureWriter.writeLeafSubstructures(entry.getKey().getAllLeafSubstructures(),
                    outputPath.resolve(clusterBaseLocation + "exemplar_" + (clusterCounter + 1) + "_" + entry.getKey() + ".pdb"));
            // write cluster members
            for (StructuralMotif structuralMotif : entry.getValue()) {
                StructureWriter.writeLeafSubstructures(structuralMotif.getAllLeafSubstructures(),
                        outputPath.resolve(clusterBaseLocation + structuralMotif + ".pdb"));
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
