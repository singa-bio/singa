package de.bioforscher.singa.chemistry.physical.model;

import java.util.Comparator;

/**
 * @author cl
 */
public class UniqueAtomIdentifer implements Comparable<UniqueAtomIdentifer> {

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int leafIdentifer;
    private final char leafInsertionCode;
    private final int atomSerial;

    public UniqueAtomIdentifer(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int leafIdentifer, char leafInsertionCode, int atomSerial) {
        this.pdbIdentifer = pdbIdentifer.toUpperCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.leafIdentifer = leafIdentifer;
        this.leafInsertionCode = leafInsertionCode;
        this.atomSerial = atomSerial;
    }

    public String getPdbIdentifier() {
        return this.pdbIdentifer;
    }

    public int getModelIdentifier() {
        return this.modelIdentifer;
    }

    public String getChainIdentifier() {
        return this.chainIdentifer;
    }

    public int getLeafIdentifer() {
        return this.leafIdentifer;
    }

    public char getLeafInsertionCode() {
        return leafInsertionCode;
    }

    public int getAtomSerial() {
        return this.atomSerial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueAtomIdentifer that = (UniqueAtomIdentifer) o;

        if (atomSerial != that.atomSerial) return false;
        if (modelIdentifer != that.modelIdentifer) return false;
        if (leafIdentifer != that.leafIdentifer) return false;
        if (leafInsertionCode != that.leafInsertionCode) return false;
        if (pdbIdentifer != null ? !pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null) return false;
        return chainIdentifer != null ? chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = pdbIdentifer != null ? pdbIdentifer.hashCode() : 0;
        result = 31 * result + modelIdentifer;
        result = 31 * result + (chainIdentifer != null ? chainIdentifer.hashCode() : 0);
        result = 31 * result + leafIdentifer;
        result = 31 * result + (int) leafInsertionCode;
        result = 31 * result + atomSerial;
        return result;
    }

    @Override
    public String toString() {
        return this.pdbIdentifer + "-" +
                this.modelIdentifer + "-" +
                this.chainIdentifer + "-" +
                this.leafIdentifer + (leafInsertionCode != 32 ? leafInsertionCode : "") + "-" +
                this.atomSerial;
    }

    @Override
    public int compareTo(UniqueAtomIdentifer o) {
        return Comparator.comparing(UniqueAtomIdentifer::getPdbIdentifier).thenComparing(UniqueAtomIdentifer::getModelIdentifier)
                .thenComparing(UniqueAtomIdentifer::getChainIdentifier).thenComparing(UniqueAtomIdentifer::getLeafIdentifer)
                .thenComparing(UniqueAtomIdentifer::getAtomSerial).compare(this, o);
    }
}
