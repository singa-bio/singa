package de.bioforscher.singa.structure.model.identifiers;

import java.util.Comparator;

/**
 * The leaf identifier consists of the PDB identifer, the model identifier, the chain identifier, the serial of a leaf
 * substructure and optionally its insertion code.
 *
 * @author cl
 */
public class LeafIdentifier implements Comparable<LeafIdentifier> {

    public static final String DEFAULT_PDB_IDENTIFIER = "0000";
    public static final int DEFAULT_MODEL_IDENTIFIER = 0;
    public static final String DEFAULT_CHAIN_IDENTIFIER = "X";
    public static final char DEFAULT_INSERTION_CODE = '\u0000';

    private static final Comparator<LeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(LeafIdentifier::getPdbIdentifier)
            .thenComparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifier)
            .thenComparing(LeafIdentifier::getSerial)
            .thenComparing(LeafIdentifier::getInsertionCode);

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int serial;
    private final char insertionCode;

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int serial, char insertionCode) {
        this.pdbIdentifer = pdbIdentifer.toLowerCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.serial = serial;
        this.insertionCode = insertionCode;
    }

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int serial) {
        this(pdbIdentifer, modelIdentifer, chainIdentifer, serial, DEFAULT_INSERTION_CODE);
    }

    public LeafIdentifier(String chainIdentifer, int serial) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, chainIdentifer, serial);
    }

    public LeafIdentifier(int serial) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, DEFAULT_CHAIN_IDENTIFIER, serial);
    }

    public static LeafIdentifier fromString(String identifier) {
        String[] split = identifier.split("-");
        return new LeafIdentifier(split[0], Integer.valueOf(split[1]));
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

    public int getSerial() {
        return this.serial;
    }

    public char getInsertionCode() {
        return this.insertionCode;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
    }

    @Override
    public String toString() {
        return this.pdbIdentifer + "-" + this.modelIdentifer + "-" + this.chainIdentifer + "-" + this.serial + (this.insertionCode != DEFAULT_INSERTION_CODE ? this.insertionCode : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafIdentifier that = (LeafIdentifier) o;

        if (this.serial != that.serial) return false;
        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.insertionCode != that.insertionCode) return false;
        if (this.pdbIdentifer != null ? !this.pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null)
            return false;
        return this.chainIdentifer != null ? this.chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer != null ? this.pdbIdentifer.hashCode() : 0;
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + (this.chainIdentifer != null ? this.chainIdentifer.hashCode() : 0);
        result = 31 * result + this.serial;
        result = 31 * result + (int) this.insertionCode;
        return result;
    }
}
