package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;

import java.util.StringJoiner;

/**
 * A data object encapsulating a match of the {@link Fit3D} algorithms.
 *
 * @author fk
 */
public class Fit3DMatch implements Comparable<Fit3DMatch> {

    private SubstructureSuperimposition substructureSuperimposition;
    private double rmsd;
    private double pvalue;

    private Fit3DMatch(SubstructureSuperimposition substructureSuperimposition, double rmsd, double pvalue) {
        this.substructureSuperimposition = substructureSuperimposition;
        this.rmsd = rmsd;
        this.pvalue = pvalue;
    }

    public static Fit3DMatch of(SubstructureSuperimposition substructureSuperimposition, double rmsd) {
        return new Fit3DMatch(substructureSuperimposition, rmsd, Double.NaN);
    }

    public static Fit3DMatch of(SubstructureSuperimposition substructureSuperimposition, double rmsd, double pvalue) {
        return new Fit3DMatch(substructureSuperimposition, rmsd, pvalue);
    }

    @Override
    public String toString() {
        return "Fit3DMatch{" +
                "substructureSuperimposition=" + substructureSuperimposition +
                ", rmsd=" + rmsd +
                ", pvalue=" + pvalue +
                '}';
    }

    public SubstructureSuperimposition getSubstructureSuperimposition() {
        return this.substructureSuperimposition;
    }

    public double getRmsd() {
        return this.rmsd;
    }

    public double getPvalue() {
        return this.pvalue;
    }

    public void setPvalue(double pvalue) {
        this.pvalue = pvalue;
    }

    public String toCsv() {
        StringJoiner stringJoiner = new StringJoiner(",");
        String stringRepresentation = this.substructureSuperimposition.getStringRepresentation();
        stringJoiner.add(stringRepresentation.replaceFirst("\\d+\\.\\d+_", ""));
        stringJoiner.add(String.valueOf(this.rmsd));
        stringJoiner.add(String.valueOf(this.pvalue));
        return stringJoiner.toString();
    }

    @Override
    public int compareTo(Fit3DMatch other) {
        return Double.compare(this.rmsd, other.rmsd);
    }
}
