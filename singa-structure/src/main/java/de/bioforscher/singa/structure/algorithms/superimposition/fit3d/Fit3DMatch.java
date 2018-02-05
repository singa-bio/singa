package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;


import de.bioforscher.singa.core.identifier.ECNumber;
import de.bioforscher.singa.core.identifier.PfamIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A data object encapsulating a match of the {@link Fit3D} algorithms.
 *
 * @author fk
 */
public class Fit3DMatch implements Comparable<Fit3DMatch> {

    public static final String CSV_HEADER = "match,rmsd,p-value\n";

    private final SubstructureSuperimposition substructureSuperimposition;
    private final double rmsd;
    private double pvalue;
    private Map<String, UniProtIdentifier> uniProtIdentifiers;
    private Map<String, PfamIdentifier> pfamIdentifiers;
    private Map<String, ECNumber> ecNumbers;

    private Fit3DMatch(double rmsd, SubstructureSuperimposition substructureSuperimposition, double pvalue) {
        this.rmsd = rmsd;
        this.substructureSuperimposition = substructureSuperimposition;
        this.pvalue = pvalue;
    }

    public static Fit3DMatch of(double rmsd) {
        return new Fit3DMatch(rmsd, null, Double.NaN);
    }

    public static Fit3DMatch of(double rmsd, SubstructureSuperimposition substructureSuperimposition) {
        return new Fit3DMatch(rmsd, substructureSuperimposition, Double.NaN);
    }

    public static Fit3DMatch of(double rmsd, SubstructureSuperimposition substructureSuperimposition, double pvalue) {
        return new Fit3DMatch(rmsd, substructureSuperimposition, pvalue);
    }

    public Optional<Map<String, UniProtIdentifier>> getUniProtIdentifiers() {
        return Optional.ofNullable(uniProtIdentifiers);
    }

    void setUniProtIdentifiers(Map<String, UniProtIdentifier> uniProtIdentifiers) {
        this.uniProtIdentifiers = uniProtIdentifiers;
    }

    public Optional<Map<String, PfamIdentifier>> getPfamIdentifiers() {
        return Optional.ofNullable(pfamIdentifiers);
    }

    void setPfamIdentifiers(Map<String, PfamIdentifier> pfamIdentifiers) {
        this.pfamIdentifiers = pfamIdentifiers;
    }

    public Optional<Map<String, ECNumber>> getEcNumbers() {
        return Optional.ofNullable(ecNumbers);
    }

    void setEcNumbers(Map<String, ECNumber> ecNumbers) {
        this.ecNumbers = ecNumbers;
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
        return substructureSuperimposition;
    }

    public double getRmsd() {
        return rmsd;
    }

    public double getPvalue() {
        return pvalue;
    }

    public void setPvalue(double pvalue) {
        this.pvalue = pvalue;
    }

    public String toCsvLine() {
        StringJoiner stringJoiner = new StringJoiner(",");
        String stringRepresentation = substructureSuperimposition.getStringRepresentation();
        stringJoiner.add(stringRepresentation.replaceFirst("\\d+\\.\\d+_", ""));
        stringJoiner.add(String.valueOf(rmsd));
        stringJoiner.add(String.valueOf(pvalue));
        return stringJoiner.toString();
    }

    @Override
    public int compareTo(Fit3DMatch other) {
        return Double.compare(rmsd, other.rmsd);
    }
}
