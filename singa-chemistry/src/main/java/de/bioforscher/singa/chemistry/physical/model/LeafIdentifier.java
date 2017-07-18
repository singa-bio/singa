package de.bioforscher.singa.chemistry.physical.model;

import java.util.Comparator;

/**
 * @author cl
 */
public class LeafIdentifier implements Comparable<LeafIdentifier> {

    public static final String DEFAULT_PDB_IDENTIFIER = "0000";
    public static final int DEFAULT_MODEL_IDENTIFIER = 0;
    public static final String DEFAULT_CHAIN_IDENTIFER = "X";
    public static final char DEFAULT_INSERTION_CODE = ' ';

    private static final Comparator<LeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(LeafIdentifier::getPdbIdentifier)
            .thenComparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifier)
            .thenComparing(LeafIdentifier::getSerial)
            .thenComparing(LeafIdentifier::getInsertionCode);

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int identifier;
    private final char insertionCode;

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int identifier, char insertionCode) {
        this.pdbIdentifer = pdbIdentifer.toLowerCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.identifier = identifier;
        this.insertionCode = insertionCode;
    }

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int identifier) {
        this(pdbIdentifer, modelIdentifer, chainIdentifer, identifier, DEFAULT_INSERTION_CODE);
    }

    public LeafIdentifier(String chainIdentifer, int identifier) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, chainIdentifer, identifier);
    }

    public LeafIdentifier(int identifier) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, DEFAULT_CHAIN_IDENTIFER, identifier);
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
        return this.identifier;
    }

    public char getInsertionCode() {
        return insertionCode;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
    }

    @Override
    public String toString() {
        return this.pdbIdentifer + "-" + this.modelIdentifer + "-" + this.chainIdentifer + "-" + this.identifier + (this.insertionCode != DEFAULT_INSERTION_CODE  ? this.insertionCode : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafIdentifier that = (LeafIdentifier) o;

        if (identifier != that.identifier) return false;
        if (modelIdentifer != that.modelIdentifer) return false;
        if (insertionCode != that.insertionCode) return false;
        if (pdbIdentifer != null ? !pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null) return false;
        return chainIdentifer != null ? chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = pdbIdentifer != null ? pdbIdentifer.hashCode() : 0;
        result = 31 * result + modelIdentifer;
        result = 31 * result + (chainIdentifer != null ? chainIdentifer.hashCode() : 0);
        result = 31 * result + identifier;
        result = 31 * result + (int) insertionCode;
        return result;
    }
}
