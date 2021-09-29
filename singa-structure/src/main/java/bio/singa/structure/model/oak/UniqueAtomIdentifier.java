package bio.singa.structure.model.oak;

import bio.singa.structure.model.interfaces.LeafIdentifier;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author cl
 */
public class UniqueAtomIdentifier implements Comparable<UniqueAtomIdentifier> {

    private final LeafIdentifier leafIdentifier;
    private final int atomSerial;

    public UniqueAtomIdentifier(String pdbIdentifer, int modelIdentifier, String chainIdentifier, int leafSerial, char leafInsertionCode, int atomSerial) {
        this(new PdbLeafIdentifier(pdbIdentifer, modelIdentifier, chainIdentifier, leafSerial, leafInsertionCode), atomSerial);
    }

    public UniqueAtomIdentifier(LeafIdentifier leafIdentifier, int atomSerial) {
        this.leafIdentifier = leafIdentifier;
        this.atomSerial = atomSerial;
    }

    public LeafIdentifier getLeafIdentifier() {
        return leafIdentifier;
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
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString(stringIdentifier.substring(0, endIndex));
        return new UniqueAtomIdentifier(leafIdentifier, Integer.parseInt(stringIdentifier.substring(endIndex + 1)));
    }

    @Override
    public int compareTo(UniqueAtomIdentifier other) {
        return Comparator.comparing(UniqueAtomIdentifier::getAtomSerial).compare(this, other);
    }
}
