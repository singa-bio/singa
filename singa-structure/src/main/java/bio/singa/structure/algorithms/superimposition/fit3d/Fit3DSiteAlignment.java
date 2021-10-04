package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.optimization.KuhnMunkres;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.mathematics.matrices.LabeledMatrix;
import bio.singa.mathematics.matrices.LabeledRegularMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimpositionException;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.scores.PsScore;
import bio.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import bio.singa.structure.algorithms.superimposition.scores.XieScore;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureWriter;
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

    private final LinkedHashSet<List<LeafSubstructure>> site1Partitions;
    private final LinkedHashSet<List<LeafSubstructure>> site2Partitions;

    private final RepresentationScheme representationScheme;
    private final Predicate<Atom> atomFilter;
    private final double rmsdCutoff;
    private final double distanceTolerance;
    private final boolean exhaustive;
    private final boolean restrictToExchanges;
    private final SubstitutionMatrix substitutionMatrix;
    private final boolean containsNonAminoAcids;
    private final boolean kuhnMunkres;

    private final double cutoffScore;
    private final List<Fit3DMatch> matches;
    private int currentAlignmentSize;
    private LabeledRegularMatrix<List<LeafSubstructure>> currentSimilarityMatrix;
    private Pair<List<LeafSubstructure>> currentBestMatchingPair;
    private double currentBestScore;
    private SubstructureSuperimposition currentBestSuperimposition;
    private String alignmentString;
    private boolean cutoffScoreReached;
    private XieScore xieScore;
    private PsScore psScore;
    private List<Pair<LeafSubstructure>> assignment;

    public Fit3DSiteAlignment(Fit3DBuilder.Builder builder) throws SubstructureSuperimpositionException {
        site1 = builder.site1.getCopy();
        site2 = builder.site2.getCopy();

        containsNonAminoAcids = site1.getAllLeafSubstructures().stream()
                .anyMatch(leafSubstructure -> !StructuralFamilies.AminoAcids.isAminoAcid(leafSubstructure.getFamily())) ||
                site1.getAllLeafSubstructures().stream()
                        .anyMatch(leafSubstructure -> !StructuralFamilies.AminoAcids.isAminoAcid(leafSubstructure.getFamily()));

        if (containsNonAminoAcids) {
            logger.warn("sites contain non-amino acid residues, no Xie and PS-scores can be calculated");
        }

        exhaustive = builder.exhaustive;
        kuhnMunkres = builder.kuhnMunkres;
        restrictToExchanges = builder.restrictToExchanges;

        // add exchanges against arbitrary types if not restricted
        if (!restrictToExchanges) {
            logger.warn("specified exchanges will be ignored for the Fit3DSite alignment and matched types will be arbitrary");
        }

        currentAlignmentSize = 2;
        currentBestScore = Double.MAX_VALUE;

        logger.debug("calculating initial 2-partitions");
        site1Partitions = createInitialPartitions(site1);
        site2Partitions = createInitialPartitions(site2);

        cutoffScore = builder.cutoffScore;

        atomFilter = builder.atomFilter;
        representationScheme = builder.representationScheme;

        rmsdCutoff = builder.rmsdCutoff;
        distanceTolerance = builder.distanceTolerance;

        substitutionMatrix = builder.substitutionMatrix;

        // initialize
        matches = new ArrayList<>();

        logger.info("computing Fit3DSite alignment for {} (size: {}) against {} (size: {}) with cutoff score {}", site1,
                site1.size(), site2, site2.size(), cutoffScore);

        // combinatorial extension alignment if Kuhn-Munkres is not used
        if (!kuhnMunkres) {
            logger.debug("using combinatorial extension to find alignment");
            calculateSimilarities();
            extendAlignment();
        } else {
            logger.debug("using Kuhn-Munkres optimization with substitution matrix {} to find alignment", substitutionMatrix);
            calculateAssignment(site1, site2);
            calculateAlignment();
        }

        Collections.sort(matches);
    }

    @Override
    public PsScore getPsScore() {
        return psScore;
    }

    @Override
    public XieScore getXieScore() {
        return xieScore;
    }

    @Override
    public String getAlignmentString() {
        return alignmentString;
    }

    /**
     * Iteratively extends the alignment until a cutoff score is exceeded or one site is fully aligned.
     */
    private void extendAlignment() {

        // iteratively extend alignment
        while (currentBestScore <= cutoffScore) {
            // terminate if one site is fully aligned
            if (site1.size() == currentAlignmentSize || site2.size() == currentAlignmentSize) {
                logger.debug("alignment fully terminated after {} iterations", currentAlignmentSize);
                break;
            }
            // extent site partitions
            extendPartitions();
            // recalculate similarities
            calculateSimilarities();
            if (cutoffScoreReached) {
                logger.debug("alignment reached cutoff score of {}", cutoffScore);
                break;
            }
        }

        if (currentBestSuperimposition != null) {
            matches.add(Fit3DMatch.of(currentBestScore, currentBestSuperimposition));
            if (!containsNonAminoAcids) {
                calculateXieScore();
                calculatePsScore();
            }
            outputSummary();
        } else {
            logger.debug("no suitable alignment could be found");
        }
    }

    private void calculatePsScore() {
        psScore = PsScore.of(currentBestSuperimposition, site1.getNumberOfLeafSubstructures(),
                site2.getNumberOfLeafSubstructures());
    }

    /**
     * Calculates the ligand binding site similarity score according to dio:10.1093/bioinformatics/btp220.
     */
    private void calculateXieScore() {
        xieScore = XieScore.of(substitutionMatrix, currentBestSuperimposition);
    }

    /**
     * Writes the result (the best largest alignment) in string representation.
     */
    private void outputSummary() {
        StringJoiner site1Joiner = new StringJoiner("|", "|", "|");
        StringJoiner site2Joiner = new StringJoiner("|", "|", "|");
        for (int i = 0; i < currentAlignmentSize; i++) {
            site1Joiner.add(String.format("%-7s", currentBestSuperimposition.getReference().get(i).toString()));
            site2Joiner.add(String.format("%-7s", currentBestSuperimposition.getCandidate().get(i).toString()));
        }
        alignmentString = String.format("%-7s", "s1size") + "|" + site1.size() + "\n" +
                String.format("%-7s", "s2size") + "|" + site2.size() + "\n" +
                site1.getAllLeafSubstructures().stream()
                        .map(LeafSubstructure::toString)
                        .map(s1 -> String.format("%-7s", s1))
                        .collect(Collectors.joining("|", String.format("%-7s", "s1") + "|", "|")) + "\n" +
                site2.getAllLeafSubstructures().stream()
                        .map(LeafSubstructure::toString)
                        .map(s1 -> String.format("%-7s", s1))
                        .collect(Collectors.joining("|", String.format("%-7s", "s2") + "|", "|")) + "\n" +
                String.format("%-7s", "RMSD") + "|" + currentBestSuperimposition.getRmsd() + "\n" +
                String.format("%-7s", "frac") + "|" + getAlignedResidueFraction() + "\n" +
                String.format("%-7s", "XieS") + "|" + (containsNonAminoAcids ? "NaN" : getXieScore().getScore()) + "\n" +
                String.format("%-7s", "XieExp") + "|" + (containsNonAminoAcids ? "NaN" : getXieScore().getSignificance()) + "\n" +
                String.format("%-7s", "PsS") + "|" + (containsNonAminoAcids ? "NaN" : getPsScore().getScore()) + "\n" +
                String.format("%-7s", "PsExp") + "|" + (containsNonAminoAcids ? "NaN" : getPsScore().getSignificance()) + "\n" +
                String.format("%-7s", "s1algn") + site1Joiner.toString() + "\n" + String.format("%-7s", "s2algn") + site2Joiner.toString();
        logger.debug("aligned {} residues (site 1 contains {} residues and site 2 contains {} residues)\n{}",
                currentAlignmentSize, site1.size(), site2.size(), alignmentString);
    }

    /**
     * Returns the fraction of successfully aligned residues.
     *
     * @return The fraction of aligned residues.
     */
    private double getAlignedResidueFraction() {
        return (site1.size() > site2.size() ? currentAlignmentSize /
                (double) site2.size() : currentAlignmentSize / (double) site1.size());
    }

    /**
     * Extends the partitions of the sites in the current round.
     */
    private void extendPartitions() {

        // increment counter for current size of alignment
        currentAlignmentSize++;

        // remove all old partitions
        site1Partitions.clear();
        site2Partitions.clear();

        // create new partitions
        for (LeafSubstructure leafSubstructure : site1.getAllLeafSubstructures()) {
            List<LeafSubstructure> site1Partition = new ArrayList<>(currentBestMatchingPair.getFirst());
            if (!site1Partition.contains(leafSubstructure)) {
                site1Partition.add(leafSubstructure);
            }
            if (site1Partition.size() == currentAlignmentSize) {
                // permute up to exhaustive cutoff to find ideal alignment seed
                if (currentAlignmentSize <= PERMUTATION_CUTOFF && !exhaustive) {
                    StreamPermutations.of(site1Partition.toArray(new LeafSubstructure[0]))
                            .map(s -> s.collect(Collectors.toList()))
                            .forEach(site1Partitions::add);
                } else {
                    site1Partitions.add(site1Partition);
                }
            }
        }
        for (LeafSubstructure leafSubstructure : site2.getAllLeafSubstructures()) {
            List<LeafSubstructure> site2Partition = new ArrayList<>(currentBestMatchingPair.getSecond());
            if (!site2Partition.contains(leafSubstructure)) {
                site2Partition.add(leafSubstructure);
            }
            if (site2Partition.size() == currentAlignmentSize) {
                // permute up to exhaustive cutoff to find ideal alignment seed
                if (currentAlignmentSize <= PERMUTATION_CUTOFF && !exhaustive) {
                    StreamPermutations.of(site2Partition.toArray(new LeafSubstructure[0]))
                            .map(s -> s.collect(Collectors.toList()))
                            .forEach(site2Partitions::add);
                } else {
                    site2Partitions.add(site2Partition);
                }
            }
        }
    }

    /**
     * Calculates the similarity scores of the current round, either by naive superimposition or with a {@link
     * Fit3DAlignment} if exchanges are defined.
     */
    private void calculateSimilarities() throws SubstructureSuperimpositionException {

        // reset best score for new iteration
        double localBestScore = Double.MAX_VALUE;

        // initialize storage for best superimposition of round
        SubstructureSuperimposition localBestSuperimposition = null;

        double[][] temporarySimilarityMatrix = new double[site1Partitions.size()][site2Partitions.size()];

        List<List<LeafSubstructure>> rowLabels = new ArrayList<>();
        List<List<LeafSubstructure>> columnLabels = new ArrayList<>();
        int i = 0;
        for (List<LeafSubstructure> site1Partition : site1Partitions) {
            rowLabels.add(site1Partition);
            int j = 0;
            for (List<LeafSubstructure> site2Partition : site2Partitions) {
                if (!columnLabels.contains(site2Partition)) {
                    columnLabels.add(site2Partition);
                }

                // use Fit3D if exchanges should be considered, otherwise use exhaustive alignment
                if (restrictToExchanges) {
                    // align the subset of sites with Fit3D
                    StructuralMotif query = site1.getCopy();
                    List<LeafSubstructure> queryLeavesToBeRemoved = site1.getAllLeafSubstructures().stream()
                            .filter(leafSubstructure -> !site1Partition.contains(leafSubstructure))
                            .collect(Collectors.toList());
                    queryLeavesToBeRemoved.forEach(leafSubstructure -> query.removeLeafSubstructure(leafSubstructure.getIdentifier()));
                    StructuralMotif target = site2.getCopy();
                    List<LeafSubstructure> targetLeavesToBeRemoved = site2.getAllLeafSubstructures().stream()
                            .filter(leafSubstructure -> !site2Partition.contains(leafSubstructure))
                            .collect(Collectors.toList());
                    targetLeavesToBeRemoved.forEach(target::removeLeafSubstructure);

                    // configure Fit3D
                    Fit3D fit3d;
                    if (representationScheme != null) {
                        fit3d = Fit3DBuilder.create()
                                .query(query)
                                .target(target)
                                .representationScheme(representationScheme.getType())
                                .rmsdCutoff(rmsdCutoff)
                                .distanceTolerance(distanceTolerance)
                                .run();
                    } else {
                        fit3d = Fit3DBuilder.create()
                                .query(query)
                                .target(target)
                                .atomFilter(atomFilter)
                                .rmsdCutoff(rmsdCutoff)
                                .distanceTolerance(distanceTolerance)
                                .run();
                    }

                    // collect results
                    if (fit3d.getMatches().isEmpty()) {
                        temporarySimilarityMatrix[i][j] = Double.MAX_VALUE;
                    } else {
                        Fit3DMatch bestMatch = fit3d.getMatches().get(0);
                        double rmsd = bestMatch.getRmsd();
                        temporarySimilarityMatrix[i][j] = rmsd;
                        // test if current score is new global winner and cutoff is not exceeded
                        if (rmsd < localBestScore) {
                            localBestSuperimposition = bestMatch.getSubstructureSuperimposition();
                            localBestScore = rmsd;
                        }
                    }
                } else {
                    SubstructureSuperimposition superimposition;
                    if (representationScheme != null) {
                        if (exhaustive) {
                            superimposition = SubstructureSuperimposer
                                    .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, representationScheme);
                        } else {
                            superimposition = SubstructureSuperimposer
                                    .calculateSubstructureSuperimposition(site1Partition, site2Partition, representationScheme);
                        }
                    } else {
                        if (exhaustive) {
                            superimposition = SubstructureSuperimposer
                                    .calculateIdealSubstructureSuperimposition(site1Partition, site2Partition, atomFilter);
                        } else {
                            superimposition = SubstructureSuperimposer
                                    .calculateSubstructureSuperimposition(site1Partition, site2Partition, atomFilter);
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

        currentSimilarityMatrix = new LabeledRegularMatrix<>(temporarySimilarityMatrix);
        currentSimilarityMatrix.setRowLabels(rowLabels);
        currentSimilarityMatrix.setColumnLabels(columnLabels);

        logger.debug("current similarity matrix is \n{}", currentSimilarityMatrix.getStringRepresentation());

        // if the minimal element is ambiguous select the first
        List<Pair<Integer>> minimalScores = Matrices.getPositionsOfMinimalElement(currentSimilarityMatrix);
        if (!minimalScores.isEmpty()) {
            List<LeafSubstructure> first = currentSimilarityMatrix.getRowLabel(minimalScores.get(0).getFirst());
            List<LeafSubstructure> second = currentSimilarityMatrix.getColumnLabel(minimalScores.get(0).getSecond());
            // if the alignment terminates in the next round do not set new best matching pair and score
            double scoreValue = currentSimilarityMatrix.getValueFromPosition(minimalScores.get(0));
            if (scoreValue > cutoffScore) {
                logger.debug("cutoff score exceeded");
                currentAlignmentSize--;
                cutoffScoreReached = true;
                return;
            }
            currentBestMatchingPair = new Pair<>(first, second);
            currentBestScore = scoreValue;
            currentBestSuperimposition = localBestSuperimposition;
            logger.debug("current best matching pair of size {} is {} with RMSD {}", currentAlignmentSize,
                    currentBestMatchingPair, currentBestScore);
        } else {
            if (currentAlignmentSize == 2) {
                throw new Fit3DException("could not find minimal agreement of partitions in first iteration");
            }
            logger.debug("no suitable alignment found in iteration {}", currentAlignmentSize);
            currentAlignmentSize--;
            currentBestScore = Double.MAX_VALUE;
        }
    }

    /**
     * Creates the initial 2-partitions of the site alignment.
     *
     * @param structuralMotif The {@link StructuralMotif} for which the 2-partitions should be created.
     * @return The generated set of 2-partitions.
     */
    private LinkedHashSet<List<LeafSubstructure>> createInitialPartitions(StructuralMotif structuralMotif) {
        LinkedHashSet<List<LeafSubstructure>> partitions = new LinkedHashSet<>();
        List<LeafSubstructure> leafSubstructures = structuralMotif.getAllLeafSubstructures();
        for (int i = 0; i < leafSubstructures.size() - 1; i++) {
            for (int j = i + 1; j < leafSubstructures.size(); j++) {
                List<LeafSubstructure> partition1 = new ArrayList<>();
                partition1.add(leafSubstructures.get(i));
                partition1.add(leafSubstructures.get(j));
                partitions.add(partition1);
                if (!exhaustive) {
                    // add first permutation of elements
                    List<LeafSubstructure> partition2 = new ArrayList<>();
                    partition2.add(leafSubstructures.get(j));
                    partition2.add(leafSubstructures.get(i));
                    partitions.add(partition2);
                }
            }
        }
        return partitions;
    }


    /**
     * Calculates the optimal assignment of the input sites using Kuhn-Munkres optimization and the given {@link SubstitutionMatrix}.
     */
    private void calculateAssignment(StructuralMotif site1, StructuralMotif site2) {
        double[][] costValues = new double[site1.size()][site2.size()];
        for (int i = 0; i < site1.getNumberOfLeafSubstructures(); i++) {
            for (int j = 0; j < site2.getNumberOfLeafSubstructures(); j++) {
                LeafSubstructure residue1 = site1.getAllLeafSubstructures().get(i);
                LeafSubstructure residue2 = site2.getAllLeafSubstructures().get(j);
                if (restrictToExchanges && residue1.getFamily() != residue2.getFamily()) {
                    // exchanges do not penalize the score
                    if (site1.getExchangeableFamilies(residue1).contains(residue2.getFamily()) ||
                            site2.getExchangeableFamilies(residue2).contains(residue1.getFamily())) {
                        continue;
                    }
                    costValues[i][j] = Double.MAX_VALUE;
                    continue;
                }
                costValues[i][j] = substitutionMatrix.getMatrix().getValueForLabel(residue1.getFamily(), residue2.getFamily());
            }
        }
        LabeledMatrix<LeafSubstructure> costMatrix = new LabeledRegularMatrix<>(costValues);
        costMatrix.setRowLabels(site1.getAllLeafSubstructures());
        costMatrix.setColumnLabels(site2.getAllLeafSubstructures());

        KuhnMunkres<LeafSubstructure> kuhnMunkres = new KuhnMunkres<>(costMatrix);
        assignment = kuhnMunkres.getAssignedPairs();

        // remove last assigned pair if strong restriction to exchanges is desired
        if (restrictToExchanges) {
            assignment.remove(assignment.size() - 1);
        }

        String assignmentString = kuhnMunkres.getAssignedPairs().stream()
                .map(pair -> pair.getFirst() + "+" + pair.getSecond() + ":" + costMatrix.getValueForLabel(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.joining("\n"));

        logger.debug("optimal assignment of binding sites is:\n{}", assignmentString);
    }


    /**
     * Calculates the alignment of sites based on the Kuhn-Munkres assignment.
     */
    private void calculateAlignment() {
        List<LeafSubstructure> reference = assignment.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
        List<LeafSubstructure> candidate = assignment.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
        currentAlignmentSize = reference.size();
        if (representationScheme != null) {
            currentBestSuperimposition = SubstructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate, representationScheme);
        } else {
            currentBestSuperimposition = SubstructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate, atomFilter);
        }
        currentBestScore = currentBestSuperimposition.getRmsd();
        matches.add(Fit3DMatch.of(currentBestSuperimposition.getRmsd(), currentBestSuperimposition));
        if (!containsNonAminoAcids) {
            calculateXieScore();
            calculatePsScore();
        }
        outputSummary();
    }


    @Override
    public void writeMatches(Path outputDirectory) {
        if (matches.isEmpty()) {
            throw new Fit3DException("cannot write matches as they are currently empty");
        }
        SubstructureSuperimposition bestSuperimposition = matches.get(0).getSubstructureSuperimposition();
        List<LeafSubstructure> mappedSite2 = bestSuperimposition.applyTo(site2.getCopy().getAllLeafSubstructures());

        String site1FileLocation = site1.getAllLeafSubstructures().stream()
                .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                .map(leafSubstructure -> leafSubstructure.getIdentifier().getChainIdentifier() + "-" + leafSubstructure.getIdentifier().getSerial())
                .collect(Collectors.joining("_", bestSuperimposition.getFormattedRmsd() + "_" + site1.getAllLeafSubstructures().get(0).getIdentifier().getStructureIdentifier() + "_", ""))
                + "_site1.pdb";

        String site2FileLocation = site2.getAllLeafSubstructures().stream()
                .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                .map(leafSubstructure -> leafSubstructure.getIdentifier().getChainIdentifier() + "-" + leafSubstructure.getIdentifier().getSerial())
                .collect(Collectors.joining("_", bestSuperimposition.getFormattedRmsd() + "_" + site2.getAllLeafSubstructures().get(0).getIdentifier().getStructureIdentifier() + "_", ""))
                + "_site2.pdb";

        try {
            StructureWriter.pdb()
                    .substructures(site1.getAllLeafSubstructures())
                    .writeToPath(outputDirectory.resolve(site1FileLocation));
            StructureWriter.pdb()
                    .substructures(mappedSite2)
                    .writeToPath(outputDirectory.resolve(site2FileLocation));
        } catch (IOException e) {
            logger.error("error writing Fit3DSite results", e);
        }
    }

    @Override
    public List<Fit3DMatch> getMatches() {
        return matches;
    }

    /**
     * Returns the fraction of aligned residues, in respect to the smaller site.
     */
    @Override
    public double getFraction() {
        return getAlignedResidueFraction();
    }
}
