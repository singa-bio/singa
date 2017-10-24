package de.bioforscher.singa.structure.algorithms.superimposition.scores;

import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

import static de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType.CA;

/**
 * An implementation of the score described in:
 * <pre>
 * Xie, L., Xie, L., and Bourne, P. E. (2009). A unified statistical model to support local sequence order independent
 * similarity searching for ligand-binding sites and its application to genome-based drug discovery.
 * Bioinformatics, 25(12), i305-i312.
 * </pre>
 *
 * @author fk
 */
public class XieScore {

    private double score;
    private double normalizedScore;
    private double significance;
    private SubstitutionMatrix substitutionMatrix;
    private SubstructureSuperimposition substructureSuperimposition;

    private XieScore(SubstitutionMatrix substitutionMatrix, SubstructureSuperimposition substructureSuperimposition) {
        this.substitutionMatrix = substitutionMatrix;
        this.substructureSuperimposition = substructureSuperimposition;
        this.score = calculateRawScore(substructureSuperimposition);
        normalizeScore();
        determineSignificance();
    }

    public static XieScore of(SubstitutionMatrix substitutionMatrix, SubstructureSuperimposition substructureSuperimposition) {
        // TODO fail with input of different size when manual constructed SubstructureSumperimposition is used
        return new XieScore(substitutionMatrix, substructureSuperimposition);
    }

    @Override
    public String toString() {
        return "XieScore{" +
                "score=" + this.score +
                ", normalizedScore=" + this.normalizedScore +
                ", significance=" + this.significance +
                ", substitutionMatrix=" + this.substitutionMatrix +
                '}';
    }

    private double calculateRawScore(SubstructureSuperimposition substructureSuperimposition) {
        RepresentationScheme xieRepresentationScheme = RepresentationSchemeFactory.createRepresentationScheme(CA);
        double temporaryScore = 0.0;
        for (int i = 0; i < substructureSuperimposition.getReference().size(); i++) {
            LeafSubstructure reference = substructureSuperimposition.getReference().get(i);
            LeafSubstructure candidate = substructureSuperimposition.getMappedCandidate().get(i);
            double m = this.substitutionMatrix.getMatrix()
                    .getValueFromPosition(this.substitutionMatrix.getMatrix()
                            .getPositionFromLabels(reference.getFamily(), candidate.getFamily()));
            double angle = xieRepresentationScheme.determineRepresentingAtom(reference).getPosition()
                    .angleTo(xieRepresentationScheme.determineRepresentingAtom(candidate).getPosition());
            if (Double.isNaN(angle)) {
                angle = 0.0;
            }
            double pa;
            if (angle > Math.PI / 2.0) {
                pa = 0.0;
            } else {
                pa = Math.cos(angle);
            }
            double pd;
            double distance = xieRepresentationScheme.determineRepresentingAtom(reference).getPosition()
                    .distanceTo(xieRepresentationScheme.determineRepresentingAtom(candidate).getPosition());
            if (distance > 4.0) {
                pd = 0.0;
            } else if (distance <= 2.0) {
                pd = 1.0;
            } else {
                pd = Math.exp(-(distance - 2.0) * (distance - 2.0) / 2.0);
            }
            temporaryScore += m * pa * pd;
        }
        return temporaryScore;
    }

    private void determineSignificance() {
        // determine logistic regression parameters based on used substitution matrix
        double a = Double.NaN;
        double b = Double.NaN;
        double c = Double.NaN;
        double d = Double.NaN;
        double e = Double.NaN;
        double f = Double.NaN;
        if (this.substitutionMatrix == SubstitutionMatrix.BLOSUM_45) {
            a = 17.242;
            b = -40.911;
            c = 46.138;
            d = 5.998;
            e = -12.370;
            f = 25.441;
        } else if (this.substitutionMatrix == SubstitutionMatrix.MC_LACHLAN) {
            a = 5.963;
            b = -15.523;
            c = 21.690;
            d = 3.122;
            e = -9.449;
            f = 18.252;
        }
        int n = this.substructureSuperimposition.getReference().size();
        double mu = a * Math.log(n) * Math.log(n) + b * Math.log(n) + c;
        double sigma = d * Math.log(n) * Math.log(n) + e * Math.log(n) + f;
        double z = (this.score * this.score - mu) / sigma;
        this.significance = 1 - Math.exp(-Math.exp(-z));
    }

    private void normalizeScore() {
        double upperBound = calculateRawScore(new SubstructureSuperimposition(0, null, null,
                this.substructureSuperimposition.getReference(), null,
                this.substructureSuperimposition.getReference(), null)) +
                calculateRawScore(new SubstructureSuperimposition(0, null, null,
                        this.substructureSuperimposition.getCandidate(), null,
                        this.substructureSuperimposition.getCandidate(), null)) / 2;
        this.normalizedScore = 1 - (this.score / upperBound);
    }

    public double getScore() {
        return this.score;
    }

    public double getSignificance() {
        return this.significance;
    }

    public double getNormalizedScore() {
        return this.normalizedScore;
    }
}
