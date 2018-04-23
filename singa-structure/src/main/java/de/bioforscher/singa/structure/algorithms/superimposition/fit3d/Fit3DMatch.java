package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;


import de.bioforscher.singa.features.identifiers.ECNumber;
import de.bioforscher.singa.features.identifiers.PfamIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A data object encapsulating a match of the {@link Fit3D} algorithms.
 *
 * @author fk
 */
public class Fit3DMatch implements Comparable<Fit3DMatch> {

    public static final String CSV_HEADER = "match,rmsd,p-value,title,UniProt,Pfam,EC\n";

    private final SubstructureSuperimposition substructureSuperimposition;
    private final double rmsd;
    private double pvalue;
    private StructuralMotif candidateMotif;
    private StructuralMotif.Type matchType;
    private Map<String, UniProtIdentifier> uniProtIdentifiers;
    private Map<String, PfamIdentifier> pfamIdentifiers;
    private Map<String, ECNumber> ecNumbers;
    private String alignedSequence;
    private String structureTitle;

    private Fit3DMatch(double rmsd, SubstructureSuperimposition substructureSuperimposition, double pvalue) {
        this.rmsd = rmsd;
        this.substructureSuperimposition = substructureSuperimposition;
        this.pvalue = pvalue;
        if (substructureSuperimposition != null) {
            analyzeMatch();
        }
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

    private void analyzeMatch() {
        alignedSequence = substructureSuperimposition.getMappedCandidate().stream()
                .map(leafSubstructure -> leafSubstructure.getFamily().getOneLetterCode())
                .collect(Collectors.joining(""));
        candidateMotif = StructuralMotif.fromLeafSubstructures(substructureSuperimposition.getMappedFullCandidate());
        matchType = StructuralMotif.Type.determine(candidateMotif);
    }

    public String getAlignedSequence() {
        return alignedSequence;
    }

    public StructuralMotif getCandidateMotif() {
        return candidateMotif;
    }

    public StructuralMotif.Type getMatchType() {
        return matchType;
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

    public String getStructureTitle() {
        return structureTitle;
    }

    void setStructureTitle(String structureTitle) {
        this.structureTitle = structureTitle;
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
        stringJoiner.add(((structureTitle != null) ? "\"" + structureTitle.replaceAll("\"", "") + "\"" : "n/a"));
        stringJoiner.add(((uniProtIdentifiers != null) ? uniProtIdentifiers.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(",", "[", "]")) : "n/a"));
        stringJoiner.add(((pfamIdentifiers != null) ? pfamIdentifiers.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(",", "[", "]")) : "n/a"));
        stringJoiner.add(((ecNumbers != null) ? ecNumbers.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(",", "[", "]")) : "n/a"));
        return stringJoiner.toString();
    }

    @Override
    public int compareTo(Fit3DMatch other) {
        return Double.compare(rmsd, other.rmsd);
    }
}
