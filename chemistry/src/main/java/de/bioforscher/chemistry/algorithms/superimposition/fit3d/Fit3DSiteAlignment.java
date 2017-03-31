package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.algorithms.superimposition.XieScore;
import de.bioforscher.chemistry.parser.pdb.structures.StructureWriter;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.families.substitution.matrices.SubstitutionMatrix;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.combinatorics.StreamPermutations;
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
 * An implementation of an algorithm for pairwise comparision of structure sites (e.g. binding sites) that are not equal
 * in size. This method iteratively extends a seed alignment until one site is fully aligned or a cutoff score is
 * reached. Internally  the {@link Fit3DAlignment} algorithm is used if exchanges should be considered.
 *
 * @author fk
 */
public class Fit3DSiteAlignment implements Fit3D {

    private static final Logger logger = LoggerFactory.getLogger(Fit3DSiteAlignment.class);
    private static final int PERMUTATION_CUTOFF = 3;

    private final StructuralMotif site1;
    private final StructuralMotif site2;

    private final LinkedHashSet<List<LeafSubstructure<?, ?>>> site1Partitions;
    private final LinkedHashSet<List<LeafSubstructure<?, ?>>> site2Partitions;

    private final RepresentationScheme representationScheme;
    private final Predicate<Atom> atomFilter;
    private final double rmsdCutoff;
    private final double distanceTolerance;
    private final boolean exhaustive;
    private final boolean restrictToExchanges;
    private final SubstitutionMatrix substitutionMatrix;

    private double cutoffScore;

    private int currentAlignmentSize;
    private LabeledRegularMatrix<List<LeafSubstructure<?, ?>>> currentSimilarityMatrix;
    private Pair<List<LeafSubstructure<?, ?>>> currentBestMatchingPair;
    private double currentBestScore;
    private SubstructureSuperimposition currentBestSuperimposition;

    private TreeMap<Double, SubstructureSuperimposition> matches;
    private String alignmentString;
    private boolean cutoffScoreReached;
    private XieScore xieScore;

    public Fit3DSiteAlignment(Fit3DBuilder.Builder builder) {
        this.site1 = builder.site1.getCopy();
        this.site2 = builder.site2.getCopy();
        this.exhaustive = builder.exhaustive;
        this.restrictToExchanges = builder.restrictToExchanges;

        // add exchanges against arbitrary types if not restricted
        if (!this.restrictToExchanges) {
            logger.info("specified exchanges will be ignored for the Fit3DSite alignment and matched types will be arbitrary");
        }

        this.currentAlignmentSize = 2;
        this.currentBestScore = Double.MAX_VALUE;

        logger.debug("calculating initial 2-partitions");
        this.site1Partitions = createInitialPartitions(this.site1);
        this.site2Partitions = createInitialPartitions(this.site2);

        this.cutoffScore = builder.cutoffScore;

        this.atomFilter = builder.atomFilter;
        this.representationScheme = builder.representationScheme;

        this.rmsdCutoff = builder.rmsdCutoff;
        this.distanceTolerance = builder.distanceTolerance;

        this.substitutionMatrix = builder.substitutionMatrix;

        // initialize
        this.matches = new TreeMap<>();

        logger.info("computing Fit3DSite alignment for {} (size: {}) against {} (size: {}) with cutoff score {}", this.site1,
                this.site1.size(), this.site2, this.site2.size(), this.cutoffScore);

        calculateSimilarities();
        extendAlignment();
    }

    @Override
    public XieScore getXieScore() {
        return this.xieScore;
    }

    @Override
    public String getAlignmentString() {
        return this.alignmentString;
    }

    /**
     * Iteratively extends the alignment until a cutoff score is exceeded or one site is fully aligned.
     */
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
            if (this.cutoffScoreReached) {
                logger.info("alignment reached cutoff score of {}", this.cutoffScore);
                break;
            }
        }

        if (this.currentBestSuperimposition != null) {
            this.matches.put(this.currentBestScore, this.currentBestSuperimposition);
            calculateXieScore();
            outputSummary();
        } else {
            logger.info("no suitable alignment could be found");
        }
    }

    /**
     * Calculates the ligand binding site similarity score according to dio:10.1093/bioinformatics/btp220.
     */
    private void calculateXieScore() {
        this.xieScore = XieScore.of(this.substitutionMatrix, this.currentBestSuperimposition);
    }

    /**
     * Writes the result (the best largest alignment) in string representation.
     */
    private void outputSummary() {
        StringJoiner site1Joiner = new StringJoiner("|", "|", "|");
        StringJoiner site2Joiner = new StringJoiner("|", "|", "|");
        for (int i = 0; i < this.currentAlignmentSize; i++) {
            site1Joiner.add(String.format("%-7s", this.currentBestSuperimposition.getReference().get(i).toString()));
            site2Joiner.add(String.format("%-7s", this.currentBestSuperimposition.getCandidate().get(i).toString()));
        }
        this.alignmentString = String.format("%-7s", "s1size") + "|" + this.site1.size() + "\n" +
                String.format("%-7s", "s2size") + "|" + this.site2.size() + "\n" +
                this.site1.getLeafSubstructures().stream()
                        .map(LeafSubstructure::toString)
                        .map(s1 -> String.format("%-7s", s1))
                        .collect(Collectors.joining("|", String.format("%-7s", "s1") + "|", "|")) + "\n" +
                this.site2.getLeafSubstructures().stream()
                        .map(LeafSubstructure::toString)
                        .map(s1 -> String.format("%-7s", s1))
                        .collect(Collectors.joining("|", String.format("%-7s", "s2") + "|", "|")) + "\n" +
                String.format("%-7s", "RMSD") + "|" + this.currentBestSuperimposition.getRmsd() + "\n" +
                String.format("%-7s", "frac") + "|" + getAlignedResidueFraction() + "\n" +
                String.format("%-7s", "XieS") + "|" + getXieScore().getScore() + "\n" +
                String.format("%-7s", "XieExp") + "|" + getXieScore().getSignificance() + "\n" +
                String.format("%-7s", "s1algn") + site1Joiner.toString() + "\n" + String.format("%-7s", "s2algn") + site2Joiner.toString();
        logger.info("aligned {} residues (site 1 contains {} residues and site 2 contains {} residues)\n{}",
                this.currentAlignmentSize, this.site1.size(), this.site2.size(), this.alignmentString);
    }

    /**
     * Returns the fraction of successfully aligned residues.
     *
     * @return The fraction of aligned residues.
     */
    private double getAlignedResidueFraction() {
        return (this.site1.size() > this.site2.size() ? this.currentAlignmentSize /
                (double) this.site2.size() : this.currentAlignmentSize / (double) this.site1.size());
    }

    /**
     * Extends the partitions of the sites in the current round.
     */
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
                // permute up to exhaustive cutoff to find ideal alignment seed
                if (this.currentAlignmentSize <= PERMUTATION_CUTOFF && !this.exhaustive) {
                    StreamPermutations.of(site1Partition.toArray(new LeafSubstructure<?, ?>[site1Partition.size()]))
                            .map(s -> s.collect(Collectors.toList()))
                            .forEach(this.site1Partitions::add);
                } else {
                    this.site1Partitions.add(site1Partition);
                }
            }
        }
        for (LeafSubstructure<?, ?> leafSubstructure : this.site2.getLeafSubstructures()) {
            List<LeafSubstructure<?, ?>> site2Partition = new ArrayList<>();
            site2Partition.addAll(this.currentBestMatchingPair.getSecond());
            if (!site2Partition.contains(leafSubstructure)) {
                site2Partition.add(leafSubstructure);
            }
            if (site2Partition.size() == this.currentAlignmentSize) {
                // permute up to exhaustive cutoff to find ideal alignment seed
                if (this.currentAlignmentSize <= PERMUTATION_CUTOFF && !this.exhaustive) {
                    StreamPermutations.of(site2Partition.toArray(new LeafSubstructure<?, ?>[site2Partition.size()]))
                            .map(s -> s.collect(Collectors.toList()))
                            .forEach(this.site2Partitions::add);
                } else {
                    this.site2Partitions.add(site2Partition);
                }
            }
        }
    }

    /**
     * Calculates the similarity scores of the current round, either by naive superimposition or with a
     * {@link Fit3DAlignment} if exchanges are defined.
     */
    private void calculateSimilarities() {

        // reset best score for new iteration
        double localBestScore = Double.MAX_VALUE;

        // initialize storage for best superimposition of round
        SubstructureSuperimposition localBestSuperimposition = null;

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

                // use Fit3D if exchanges should be considered, otherwise use exhaustive alignment
                if (this.restrictToExchanges) {
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

                    // configure Fit3D
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
                        if (rmsd < localBestScore) {
                            localBestSuperimposition = fit3d.getMatches().firstEntry().getValue();
                            localBestScore = rmsd;
                        }
                    }
                } else {
                    SubstructureSuperimposition superimposition;
                    if (this.representationScheme != null) {
                        if (this.exhaustive) {
                            superimposition = SubStructureSuperimposer
                                    .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, this.representationScheme);
                        } else {
                            superimposition = SubStructureSuperimposer
                                    .calculateSubstructureSuperimposition(site1Partition, site2Partition, this.representationScheme);
                        }
                    } else {
                        if (this.exhaustive) {
                            superimposition = SubStructureSuperimposer
                                    .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, this.atomFilter);
                        } else {
                            superimposition = SubStructureSuperimposer
                                    .calculateSubstructureSuperimposition(site1Partition, site2Partition, this.atomFilter);
                        }
                    }
                    double rmsd = superimposition.getRmsd();
                    temporarySimilarityMatrix[i][j] = rmsd;
                    if (rmsd < localBestScore) {
                        localBestSuperimposition = superimposition;
                        localBestScore = rmsd;
                    }
                }
                j++;
            }
            i++;
        }

        this.currentSimilarityMatrix = new LabeledRegularMatrix<>(temporarySimilarityMatrix);
        this.currentSimilarityMatrix.setRowLabels(rowLabels);
        this.currentSimilarityMatrix.setColumnLabels(columnLabels);

        logger.debug("current similarity matrix is \n{}", this.currentSimilarityMatrix.getStringRepresentation());

        // if the minimal element is ambiguous select the first
        List<Pair<Integer>> minimalScores = Matrices.getPositionsOfMinimalElement(this.currentSimilarityMatrix);
        if (!minimalScores.isEmpty()) {
            List<LeafSubstructure<?, ?>> first = this.currentSimilarityMatrix.getRowLabel(minimalScores.get(0).getFirst());
            List<LeafSubstructure<?, ?>> second = this.currentSimilarityMatrix.getColumnLabel(minimalScores.get(0).getSecond());
            // if the alignment terminates in the next round do not set new best matching pair and score
            double scoreValue = this.currentSimilarityMatrix.getValueFromPosition(minimalScores.get(0));
            if (scoreValue > this.cutoffScore) {
                logger.info("cutoff score exceeded");
                this.currentAlignmentSize--;
                this.cutoffScoreReached = true;
                return;
            }
            this.currentBestMatchingPair = new Pair<>(first, second);
            this.currentBestScore = scoreValue;
            this.currentBestSuperimposition = localBestSuperimposition;
            logger.info("current best matching pair of size {} is {} with RMSD {}", this.currentAlignmentSize,
                    this.currentBestMatchingPair, this.currentBestScore);
        } else {
            if (this.currentAlignmentSize == 2) {
                throw new Fit3DException("could not find minimal agreement of partitions in first iteration");
            }
            logger.info("no suitable alignment found in iteration {}", this.currentAlignmentSize);
            this.currentAlignmentSize--;
            this.currentBestScore = Double.MAX_VALUE;
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
                List<LeafSubstructure<?, ?>> partition1 = new ArrayList<>();
                partition1.add(leafSubstructures.get(i));
                partition1.add(leafSubstructures.get(j));
                partitions.add(partition1);
                if (!this.exhaustive) {
                    // add first permutation of elements
                    List<LeafSubstructure<?, ?>> partition2 = new ArrayList<>();
                    partition2.add(leafSubstructures.get(j));
                    partition2.add(leafSubstructures.get(i));
                    partitions.add(partition2);
                }
            }
        }
        return partitions;
    }

    @Override
    public void writeMatches(Path outputDirectory) {
        if (this.matches.isEmpty()) {
            throw new Fit3DException("cannot write matches as they are currently empty");
        }
        SubstructureSuperimposition bestSuperimposition = this.matches.firstEntry().getValue();
        List<LeafSubstructure<?, ?>> mappedSite2 = bestSuperimposition.applyTo(this.site2.getCopy().getLeafSubstructures());
        try {
            StructureWriter.writeLeafSubstructures(this.site1.getLeafSubstructures(),
                    outputDirectory.resolve(this.site1.getLeafSubstructures().stream()
                            .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                            .map(Object::toString)
                            .collect(Collectors.joining("_", bestSuperimposition.getFormattedRmsd() + "_"
                                    + this.site1.getLeafSubstructures().get(0).getPdbIdentifier()
                                    + "|", "")) + "_site1.pdb"));
            StructureWriter.writeLeafSubstructures(mappedSite2,
                    outputDirectory.resolve(this.site2.getLeafSubstructures().stream()
                            .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                            .map(Object::toString)
                            .collect(Collectors.joining("_", bestSuperimposition.getFormattedRmsd() + "_"
                                    + this.site2.getLeafSubstructures().get(0).getPdbIdentifier()
                                    + "|", "")) + "_site2.pdb"));
        } catch (IOException e) {
            logger.error("error writing Fit3DSite results", e);
        }
    }

    @Override
    public TreeMap<Double, SubstructureSuperimposition> getMatches() {
        return this.matches;
    }

    /**
     * Returns the fraction of aligned residues, in respect to the smaller site.
     */
    @Override
    public double getFraction() {
        return getAlignedResidueFraction();
    }
}
