package bio.singa.features.identifiers;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author cl
 */
public class UniqueAtomIdentifier implements Comparable<UniqueAtomIdentifier> {

    private final LeafIdentifier leafIdentifier;
    private final int atomSerial;

    public UniqueAtomIdentifier(String pdbIdentifer, int modelIdentifier, String chainIdentifier, int leafSerial, char leafInsertionCode, int atomSerial) {
        this(new LeafIdentifier(pdbIdentifer, modelIdentifier, chainIdentifier, leafSerial, leafInsertionCode), atomSerial);
    }

    public UniqueAtomIdentifier(LeafIdentifier leafIdentifier, int atomSerial) {
        this.leafIdentifier = leafIdentifier;
        this.atomSerial = atomSerial;
    }

    public LeafIdentifier getLeafIdentifier() {
        return leafIdentifier;
    }

    public String getPdbIdentifier() {
        return leafIdentifier.getPdbIdentifier();
    }

    public int getModelIdentifier() {
        return leafIdentifier.getModelIdentifier();
    }

    public String getChainIdentifier() {
        return leafIdentifier.getChainIdentifier();
    }

    public int getLeafSerial() {
        return leafIdentifier.getSerial();
    }

    public char getLeafInsertionCode() {
        return leafIdentifier.getInsertionCode();
    }

    public int getAtomSerial() {
        return atomSerial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueAtomIdentifier that = (UniqueAtomIdentifier) o;
        return atomSerial == that.atomSerial && Objects.equals(leafIdentifier, that.leafIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leafIdentifier, atomSerial);
    }

    @Override
    public String toString() {
        return leafIdentifier.toString() + "-" + atomSerial;
    }

    public static UniqueAtomIdentifier fromString(String stringIdentifier) {
        int endIndex = stringIdentifier.lastIndexOf("-");
        LeafIdentifier leafIdentifier = LeafIdentifier.fromString(stringIdentifier.substring(0, endIndex));
        return new UniqueAtomIdentifier(leafIdentifier, Integer.parseInt(stringIdentifier.substring(endIndex + 1)));
    }

    @Override
    public int compareTo(UniqueAtomIdentifier other) {
        return Comparator.comparing(UniqueAtomIdentifier::getAtomSerial).compare(this, other);
    }
}
