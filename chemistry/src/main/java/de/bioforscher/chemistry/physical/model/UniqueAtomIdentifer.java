package de.bioforscher.chemistry.physical.model;

import java.util.Comparator;

/**
 * @author cl
 */
public class UniqueAtomIdentifer implements Comparable<UniqueAtomIdentifer> {

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int leafIdentifer;
    private final int atomSerial;

    public UniqueAtomIdentifer(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int leafIdentifer, int atomSerial) {
        this.pdbIdentifer = pdbIdentifer.toUpperCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.leafIdentifer = leafIdentifer;
        this.atomSerial = atomSerial;
    }

    public String getPdbIdentifer() {
        return this.pdbIdentifer;
    }

    public int getModelIdentifer() {
        return this.modelIdentifer;
    }

    public String getChainIdentifer() {
        return this.chainIdentifer;
    }

    public int getLeafIdentifer() {
        return this.leafIdentifer;
    }

    public int getAtomSerial() {
        return this.atomSerial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueAtomIdentifer that = (UniqueAtomIdentifer) o;

        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.leafIdentifer != that.leafIdentifer) return false;
        if (this.atomSerial != that.atomSerial) return false;
        if (this.pdbIdentifer != null ? !this.pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null) return false;
        return this.chainIdentifer != null ? this.chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer != null ? this.pdbIdentifer.hashCode() : 0;
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + (this.chainIdentifer != null ? this.chainIdentifer.hashCode() : 0);
        result = 31 * result + this.leafIdentifer;
        result = 31 * result + this.atomSerial;
        return result;
    }

    @Override
    public String toString() {
        return this.pdbIdentifer +"-"+this.modelIdentifer+"-"+this.chainIdentifer+"-"+this.leafIdentifer+"-"+ this.atomSerial;
    }

    @Override
    public int compareTo(UniqueAtomIdentifer o) {
        return Comparator.comparing(UniqueAtomIdentifer::getPdbIdentifer).thenComparing(UniqueAtomIdentifer::getModelIdentifer)
                .thenComparing(UniqueAtomIdentifer::getChainIdentifer).thenComparing(UniqueAtomIdentifer::getLeafIdentifer)
                .thenComparing(UniqueAtomIdentifer::getAtomSerial).compare(this, o);
    }
}
