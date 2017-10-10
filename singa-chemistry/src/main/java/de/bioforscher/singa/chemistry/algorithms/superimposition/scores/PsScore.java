package de.bioforscher.singa.chemistry.algorithms.superimposition.scores;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * An implementation of the score to assess the similarity of aligned ligand binding site environments:
 * <pre>
 *     Gao, M., and Skolnick, J. (2013). APoc: large-scale identification of similar protein pockets.
 *     Bioinformatics, 29(5), 597-604.
 * </pre>
 *
 * @author fk
 */
public class PsScore {

    private static final List<EnumSet<AminoAcidFamily>> AMINO_ACID_GROUPS;

    static {
        AMINO_ACID_GROUPS = new ArrayList<>();
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.LEUCINE, AminoAcidFamily.VALINE, AminoAcidFamily.ISOLEUCINE, AminoAcidFamily.METHIONINE, AminoAcidFamily.CYSTEINE));
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.ALANINE, AminoAcidFamily.GLYCINE));
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.SERINE, AminoAcidFamily.THREONINE));
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.PROLINE));
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.PHENYLALANINE, AminoAcidFamily.TYROSINE, AminoAcidFamily.TRYPTOPHAN));
        AMINO_ACID_GROUPS.add(EnumSet.of(AminoAcidFamily.GLUTAMIC_ACID, AminoAcidFamily.ASPARTIC_ACID, AminoAcidFamily.ASPARAGINE, AminoAcidFamily.GLUTAMINE));
    }

    private final int referenceLength;
    private final int queryLength;
    private final RepresentationScheme alphaCarbonRepresentation;
    private final RepresentationScheme betaCarbonRepresentation;
    private SubstructureSuperimposition substructureSuperimposition;
    private double score;
    private double significance;

    private PsScore(SubstructureSuperimposition substructureSuperimposition, int referenceLength, int queryLength) {
        this.substructureSuperimposition = substructureSuperimposition;
        this.referenceLength = referenceLength;
        this.queryLength = queryLength;
        this.alphaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CA);
        this.betaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CB);
        calculateScore();
        calculateSignificance();
    }

    private static boolean belongToSameGroup(Pair<LeafSubstructure<?, ?>> leafSubstructurePair) {
        return AMINO_ACID_GROUPS.stream()
                .anyMatch(group -> group.contains(leafSubstructurePair.getFirst().getFamily())
                        && group.contains(leafSubstructurePair.getSecond().getFamily()));
    }

    /**
     * Calculates the {@link PsScore} for the given {@link SubstructureSuperimposition}.
     *
     * @param substructureSuperimposition The {@link SubstructureSuperimposition} for which the score should be
     * calculated.
     * @param referenceLength The length of the reference ligand binding site.
     * @param queryLength The length of the query ligand binding site.
     * @return The calculated {@link PsScore}.
     */
    public static PsScore of(SubstructureSuperimposition substructureSuperimposition, int referenceLength, int queryLength) {
        return new PsScore(substructureSuperimposition, referenceLength, queryLength);
    }

    private void calculateSignificance() {
        this.significance = 1 - Math.exp(-Math.exp(-calculateZ()));
    }

    private double calculateZ() {
        double mu = 0.3117 + 0.0277 * Math.log(this.queryLength) + (-0.029) * Math.log(this.referenceLength);
        double sigma = 0.0366 + 0.0025 * Math.log(this.queryLength) + (-0.0084) * Math.log(this.referenceLength);
        return (this.score - mu) / sigma;
    }

    private void calculateScore() {
        double scalingFactor = 0.23 - 12.0 / Math.pow(this.queryLength, 1.88);
        this.score = (determineS() + scalingFactor) / (1 + scalingFactor);
    }

    private double determineS() {

        double sumValue = 0.0;
        for (int i = 0; i < this.substructureSuperimposition.getReference().size(); i++) {
            LeafSubstructure<?, ?> referenceLeafSubstructure = this.substructureSuperimposition.getReference().get(i);
            LeafSubstructure<?, ?> queryLeafSubstructure = this.substructureSuperimposition.getMappedCandidate().get(i);

            double distance = VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistance(referenceLeafSubstructure.getPosition(), queryLeafSubstructure.getPosition());
            double distanceScalingFactor = 0.70 * Math.pow((this.queryLength - 5), 0.25) - 0.2;
            double pi = calculateP(referenceLeafSubstructure, queryLeafSubstructure);
            double ri = calculateR(referenceLeafSubstructure, queryLeafSubstructure);
            sumValue += pi * ri * (1 + Math.pow(distance, 2) / Math.pow(distanceScalingFactor, 2));
        }

        return (1.0 / this.queryLength) * sumValue;
    }

    private double calculateR(LeafSubstructure<?, ?> referenceLeafSubstructure, LeafSubstructure<?, ?> querylLeafSubstructure) {
        boolean belongToSameGroup = belongToSameGroup(new Pair<>(referenceLeafSubstructure, querylLeafSubstructure));
        return Math.max(0.8, belongToSameGroup ? 1.0 : 0.0);
    }

    private double calculateP(LeafSubstructure<?, ?> referenceLeafSubstructure, LeafSubstructure<?, ?> querylLeafSubstructure) {
        if (referenceLeafSubstructure.getFamily() != AminoAcidFamily.GLYCINE
                && querylLeafSubstructure.getFamily() != AminoAcidFamily.GLYCINE) {
            double theta = determineAlphaBetaVector(referenceLeafSubstructure).angleTo(determineAlphaBetaVector(querylLeafSubstructure));
            if (theta <= Math.PI / 3.0) {
                return 1.0;
            } else {
                return Math.max(0.1, 0.5 + Math.cos(theta));
            }
        } else if (referenceLeafSubstructure.getFamily() == AminoAcidFamily.GLYCINE
                && querylLeafSubstructure.getFamily() != AminoAcidFamily.GLYCINE) {
            return 0.77;
        } else {
            return 1.0;
        }
    }

    private Vector3D determineAlphaBetaVector(LeafSubstructure<?, ?> leafSubstructure) {
        return this.alphaCarbonRepresentation.determineRepresentingAtom(leafSubstructure).getPosition()
                .subtract(this.betaCarbonRepresentation.determineRepresentingAtom(leafSubstructure).getPosition());
    }

    public double getScore() {
        return this.score;
    }

    public double getSignificance() {
        return this.significance;
    }

    @Override
    public String toString() {
        return "PsScore{" +
                "referenceLength=" + this.referenceLength +
                ", queryLength=" + this.queryLength +
                ", score=" + this.score +
                ", significance=" + this.significance +
                '}';
    }
}
