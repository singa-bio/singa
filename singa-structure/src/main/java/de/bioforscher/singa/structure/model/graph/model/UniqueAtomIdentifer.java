package de.bioforscher.singa.structure.model.graph.model;

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
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.leafSerial = leafSerial;
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

    public int getLeafSerial() {
        return this.leafSerial;
    }

    public char getLeafInsertionCode() {
        return this.leafInsertionCode;
    }

    public int getAtomSerial() {
        return this.atomSerial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueAtomIdentifer that = (UniqueAtomIdentifer) o;

        if (this.atomSerial != that.atomSerial) return false;
        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.leafSerial != that.leafSerial) return false;
        if (this.leafInsertionCode != that.leafInsertionCode) return false;
        if (this.pdbIdentifer != null ? !this.pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null) return false;
        return this.chainIdentifer != null ? this.chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer != null ? this.pdbIdentifer.hashCode() : 0;
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + (this.chainIdentifer != null ? this.chainIdentifer.hashCode() : 0);
        result = 31 * result + this.leafSerial;
        result = 31 * result + (int) this.leafInsertionCode;
        result = 31 * result + this.atomSerial;
        return result;
    }

    @Override
    public String toString() {
        return this.pdbIdentifer + "-" +
                this.modelIdentifer + "-" +
                this.chainIdentifer + "-" +
                this.leafSerial + (this.leafInsertionCode != 32 ? this.leafInsertionCode : "") + "-" +
                this.atomSerial;
    }

    @Override
    public int compareTo(UniqueAtomIdentifer other) {
        return Comparator.comparing(UniqueAtomIdentifer::getAtomSerial).compare(this, other);
    }
}
