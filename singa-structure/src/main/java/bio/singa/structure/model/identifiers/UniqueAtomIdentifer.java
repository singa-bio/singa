package bio.singa.structure.model.identifiers;

import java.util.Comparator;

/**
 * @author cl
 */
public class UniqueAtomIdentifer implements Comparable<UniqueAtomIdentifer> {

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int leafSerial;
    private final char leafInsertionCode;
    private final int atomSerial;

    public UniqueAtomIdentifer(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int leafSerial, char leafInsertionCode, int atomSerial) {
        this.pdbIdentifer = pdbIdentifer.toUpperCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer;
        this.leafSerial = leafSerial;
        this.leafInsertionCode = leafInsertionCode;
        this.atomSerial = atomSerial;
    }

    public String getPdbIdentifier() {
        return pdbIdentifer;
    }

    public int getModelIdentifier() {
        return modelIdentifer;
    }

    public String getChainIdentifier() {
        return chainIdentifer;
    }

    public int getLeafSerial() {
        return leafSerial;
    }

    public char getLeafInsertionCode() {
        return leafInsertionCode;
    }

    public int getAtomSerial() {
        return atomSerial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueAtomIdentifer that = (UniqueAtomIdentifer) o;

        if (atomSerial != that.atomSerial) return false;
        if (modelIdentifer != that.modelIdentifer) return false;
        if (leafSerial != that.leafSerial) return false;
        if (leafInsertionCode != that.leafInsertionCode) return false;
        if (pdbIdentifer != null ? !pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null)
            return false;
        return chainIdentifer != null ? chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = pdbIdentifer != null ? pdbIdentifer.hashCode() : 0;
        result = 31 * result + modelIdentifer;
        result = 31 * result + (chainIdentifer != null ? chainIdentifer.hashCode() : 0);
        result = 31 * result + leafSerial;
        result = 31 * result + (int) leafInsertionCode;
        result = 31 * result + atomSerial;
        return result;
    }

    @Override
    public String toString() {
        return pdbIdentifer + "-" +
                modelIdentifer + "-" +
                chainIdentifer + "-" +
                leafSerial + (leafInsertionCode != LeafIdentifier.DEFAULT_INSERTION_CODE ? leafInsertionCode : "") + "-" +
                atomSerial;
    }

    @Override
    public int compareTo(UniqueAtomIdentifer other) {
        return Comparator.comparing(UniqueAtomIdentifer::getAtomSerial).compare(this, other);
    }
}
