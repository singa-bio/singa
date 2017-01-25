package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.structures.PDBWriterService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.matrices.LabeledRegularMatrix;
import de.bioforscher.mathematics.matrices.Matrices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class Fit3DSiteAlignment implements Fit3D {

    private static final Logger logger = LoggerFactory.getLogger(Fit3DSiteAlignment.class);
    private static final double DEFAULT_CUTOFF_SCORE = 0.6;

    private final StructuralMotif site1;
    private final StructuralMotif site2;

    private final LinkedHashSet<List<LeafSubstructure<?, ?>>> site1Partitions;
    private final LinkedHashSet<List<LeafSubstructure<?, ?>>> site2Partitions;

    private final RepresentationScheme representationScheme;
    private final Predicate<Atom> atomFilter;
    private final double rmsdCutoff;
    private final double distanceTolerance;

    private double cutoffScore = DEFAULT_CUTOFF_SCORE;

    private int currentAlignmentSize;
    private LabeledRegularMatrix<List<LeafSubstructure<?, ?>>> currentSimilarityMatrix;
    private Pair<List<LeafSubstructure<?, ?>>> currentBestMatchingPair;
    private double currentBestScore;
    private SubstructureSuperimposition currentBestSuperimposition;

    private TreeMap<Double, SubstructureSuperimposition> matches;

    public Fit3DSiteAlignment(Fit3DBuilder.Builder builder) {
        this.site1 = builder.site1.getCopy();
        this.site2 = builder.site2.getCopy();

        logger.info("computing Fit3DSite alignment for {} against {}", this.site1, this.site2);

        this.currentAlignmentSize = 2;
        this.currentBestScore = Double.MAX_VALUE;

        logger.info("calculating initial 2-partitions");
        this.site1Partitions = createInitialPartitions(this.site1);
        this.site2Partitions = createInitialPartitions(this.site2);

        this.atomFilter = builder.atomFilter;
        this.representationScheme = builder.representationScheme;

        this.rmsdCutoff = builder.rmsdCutoff;
        this.distanceTolerance = builder.distanceTolerance;

        calculateSimilarities();
        extendAlignment();
    }

    private void extendAlignment() {

        // iteratively extend alignment
        while (this.currentBestScore <= this.cutoffScore) {
            // terminate if one site is fully aligned
            if (this.site1.size() == this.currentAlignmentSize || this.site2.size() == this.currentAlignmentSize) {
                logger.info("alignment fully terminated after {} iterations", this.currentAlignmentSize);
                break;
            }
            // extent site partitions
            extendPartitions();
            // recalculate similarities
            calculateSimilarities();
            if (this.currentBestScore > DEFAULT_CUTOFF_SCORE) {
                logger.info("alignment reached cutoff score of {}", this.cutoffScore);
            }
        }

        this.matches = new TreeMap<>();
        this.matches.put(this.currentBestScore, this.currentBestSuperimposition);

        outputSummary();
    }

    private void outputSummary() {
        StringJoiner site1Joiner = new StringJoiner("|", "|", "|");
        StringJoiner site2Joiner = new StringJoiner("|", "|", "|");
        for (int i = 0; i < this.currentAlignmentSize; i++) {
            site1Joiner.add(String.format("%-7s", this.currentBestMatchingPair.getFirst().get(i).toString()));
            site2Joiner.add(String.format("%-7s", this.currentBestSuperimposition.getMappedCandidate().get(i).toString()));
        }
        logger.info("aligned {} residues (site 1 contains {} residues and site 2 contains {} residues): \nsite 1: {}\nsite 2: {}",
                this.currentAlignmentSize, this.site1.size(), this.site2.size(), site1Joiner.toString(),
                site2Joiner.toString());
    }

    private void extendPartitions() {

        // increment counter for current size of alignment
        this.currentAlignmentSize++;

        // remove all old partitions
        this.site1Partitions.clear();
        this.site2Partitions.clear();

        // create new partitions
        for (LeafSubstructure<?, ?> leafSubstructure : this.site1.getLeafSubstructures()) {
            List<LeafSubstructure<?, ?>> site1Partition = new ArrayList<>();
            site1Partition.addAll(this.currentBestMatchingPair.getFirst());
            if (!site1Partition.contains(leafSubstructure)) {
                site1Partition.add(leafSubstructure);
            }
            if (site1Partition.size() == this.currentAlignmentSize) {
                this.site1Partitions.add(site1Partition);
            }
        }
        for (LeafSubstructure<?, ?> leafSubstructure : this.site2.getLeafSubstructures()) {
            List<LeafSubstructure<?, ?>> site2Partition = new ArrayList<>();
            site2Partition.addAll(this.currentBestMatchingPair.getSecond());
            if (!site2Partition.contains(leafSubstructure)) {
                site2Partition.add(leafSubstructure);
            }
            if (site2Partition.size() == this.currentAlignmentSize) {
                this.site2Partitions.add(site2Partition);
            }
        }
    }

    private void calculateSimilarities() {

        // reset best score for new iteration
        this.currentBestScore = Double.MAX_VALUE;

        double[][] temporarySimilarityMatrix = new double[this.site1Partitions.size()][this.site2Partitions.size()];

        List<List<LeafSubstructure<?, ?>>> rowLabels = new ArrayList<>();
        List<List<LeafSubstructure<?, ?>>> columnLabels = new ArrayList<>();
        int i = 0;
        for (List<LeafSubstructure<?, ?>> site1Partition : this.site1Partitions) {
            rowLabels.add(site1Partition);
            int j = 0;
            for (List<LeafSubstructure<?, ?>> site2Partition : this.site2Partitions) {
                if (!columnLabels.contains(site2Partition)) {
                    columnLabels.add(site2Partition);
                }

                // align the subset of sites with Fit3D
                StructuralMotif query = this.site1.getCopy();
                List<LeafIdentifier> queryLeavesToBeRemoved = this.site1.getLeafSubstructures().stream()
                        .filter(leafSubstructure -> !site1Partition.contains(leafSubstructure))
                        .map(LeafSubstructure::getLeafIdentifier)
                        .collect(Collectors.toList());
                queryLeavesToBeRemoved.forEach(query::removeLeafSubstructure);
                StructuralMotif target = this.site2.getCopy();
                List<LeafIdentifier> targetLeavesToBeRemoved = this.site2.getLeafSubstructures().stream()
                        .filter(leafSubstructure -> !site2Partition.contains(leafSubstructure))
                        .map(LeafSubstructure::getLeafIdentifier)
                        .collect(Collectors.toList());
                targetLeavesToBeRemoved.forEach(target::removeLeafSubstructure);

                // configure Fit3D to use exhaustive search
                Fit3D fit3d;
                if (this.representationScheme != null) {
                    fit3d = Fit3DBuilder.create()
                            .query(query)
                            .target(target)
                            .representationScheme(this.representationScheme.getType())
                            .rmsdCutoff(this.rmsdCutoff)
                            .distanceTolerance(this.distanceTolerance)
                            .run();
                } else {
                    fit3d = Fit3DBuilder.create()
                            .query(query)
                            .target(target)
                            .atomFilter(this.atomFilter)
                            .rmsdCutoff(this.rmsdCutoff)
                            .distanceTolerance(this.distanceTolerance)
                            .run();
                }

                // collect results
                if (fit3d.getMatches().isEmpty()) {
                    temporarySimilarityMatrix[i][j] = Double.MAX_VALUE;
                } else {
                    double rmsd = fit3d.getMatches().firstKey();
                    temporarySimilarityMatrix[i][j] = rmsd;
                    // test if current score is new global winner and cutoff is not exceeded
                    if (rmsd < this.currentBestScore && rmsd <= this.cutoffScore) {
                        this.currentBestSuperimposition = fit3d.getMatches().firstEntry().getValue();
                    }
                }

//                SubstructureSuperimposition superimposition;
//                if (this.representationScheme != null) {
//                    superimposition = SubStructureSuperimposer
//                            .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, this.representationScheme);
//                } else {
//                    superimposition = SubStructureSuperimposer
//                            .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, this.atomFilter);
//                }
//                double rmsd = superimposition.getRmsd();
//                temporarySimilarityMatrix[i][j] = rmsd;
//                if (rmsd < this.currentBestScore && rmsd <= this.cutoffScore) {
//                    this.currentBestSuperimposition = superimposition;
//                }
                j++;
            }
            i++;
        }

        this.currentSimilarityMatrix = new LabeledRegularMatrix<>(temporarySimilarityMatrix);
        this.currentSimilarityMatrix.setRowLabels(rowLabels);
        this.currentSimilarityMatrix.setColumnLabels(columnLabels);
        logger.info("current similarity matrix is \n{}", this.currentSimilarityMatrix.getStringRepresentation());
        Optional<Pair<Integer>> minimalScore = Matrices.getPositionOfMinimalElement(this.currentSimilarityMatrix);
        if (minimalScore.isPresent()) {
            List<LeafSubstructure<?, ?>> first = this.currentSimilarityMatrix.getRowLabel(minimalScore.get().getFirst());
            List<LeafSubstructure<?, ?>> second = this.currentSimilarityMatrix.getColumnLabel(minimalScore.get().getSecond());
            // if the alignment terminates in the next round do not set new best matching pair and score
            double scoreValue = this.currentSimilarityMatrix.getValueFromPosition(minimalScore.get());
            if (scoreValue > this.cutoffScore) {
                this.currentAlignmentSize--;
                return;
            }
            this.currentBestMatchingPair = new Pair<>(first, second);
            this.currentBestScore = scoreValue;
            // FIXME currentBestSuperimposition is also not allowed to be set if cutoff exceeded
            logger.info("current best matching pair of size {} is {} with RMSD {}", this.currentAlignmentSize,
                    this.currentBestMatchingPair, this.currentBestScore);
        } else {
            throw new Fit3DException("could not find minimal agreement of partitions in iteration " +
                    this.currentAlignmentSize);
        }
    }

    /**
     * Creates the initial 2-partitions of the site alignment.
     *
     * @param structuralMotif The {@link StructuralMotif} for which the 2-partitions should be created.
     * @return The generated set of 2-partitions.
     */
    private LinkedHashSet<List<LeafSubstructure<?, ?>>> createInitialPartitions(StructuralMotif structuralMotif) {
        LinkedHashSet<List<LeafSubstructure<?, ?>>> partitions = new LinkedHashSet<>();
        List<LeafSubstructure<?, ?>> leafSubstructures = structuralMotif.getLeafSubstructures();
        for (int i = 0; i < leafSubstructures.size() - 1; i++) {
            for (int j = i + 1; j < leafSubstructures.size(); j++) {
                List<LeafSubstructure<?, ?>> partition = new ArrayList<>();
                partition.add(leafSubstructures.get(i));
                partition.add(leafSubstructures.get(j));
                partitions.add(partition);
            }
        }
        return partitions;
    }

    @Override
    public void writeMatches(Path outputDirectory) {
        SubstructureSuperimposition bestSuperimposition = this.matches.firstEntry().getValue();
        List<LeafSubstructure<?, ?>> mappedSite2 = bestSuperimposition.applyTo(this.site2.getCopy().getLeafSubstructures());
        try {
            PDBWriterService.writeLeafSubstructures(this.site1.getLeafSubstructures(),
                    outputDirectory.resolve(bestSuperimposition.getStringRepresentation() + "_site1.pdb"));
            PDBWriterService.writeLeafSubstructures(mappedSite2,
                    outputDirectory.resolve(bestSuperimposition.getStringRepresentation() + "_site2.pdb"));
        } catch (IOException e) {
            logger.error("error writing Fit3DSite results", e);
        }
    }

    @Override
    public TreeMap<Double, SubstructureSuperimposition> getMatches() {
        return this.matches;
    }
}
