package de.bioforscher.chemistry.algorithms.superimposition;

/**
 * An implementation of the score described in:
 * <p>
 * <pre>
 * Xie, L., Xie, L., & Bourne, P. E. (2009). A unified statistical model to support local sequence order independent
 * similarity searching for ligand-binding sites and its application to genome-based drug discovery.
 * Bioinformatics, 25(12), i305-i312.
 * </pre>
 *
 * @author fk
 */
public class XieScore {

    private double score;
    private double significance;

    public double getScore() {
        return this.score;
    }

    public double getSignificance() {
        return this.significance;
    }
}
