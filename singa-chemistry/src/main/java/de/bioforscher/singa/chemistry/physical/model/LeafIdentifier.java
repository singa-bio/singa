package de.bioforscher.singa.chemistry.physical.model;

import java.util.Comparator;

/**
 * @author cl
 */
public class LeafIdentifier implements Comparable<LeafIdentifier> {

    public static final String DEFAULT_PDB_IDENTIFIER = "0000";
    public static final int DEFAULT_MODEL_IDENTIFIER = 0;
    public static final String DEFAULT_CHAIN_IDENTIFER = "X";
    private static final Comparator<LeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(LeafIdentifier::getPdbIdentifier)
            .thenComparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifer)
            .thenComparing(LeafIdentifier::getIdentifier);
    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int identifier;

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int identifier) {
        this.pdbIdentifer = pdbIdentifer.toUpperCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.identifier = identifier;
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

    public String getChainIdentifer() {
        return this.chainIdentifer;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
    }

    @Override
    public String toString() {
        return this.pdbIdentifer + "-" + this.modelIdentifer + "-" + this.chainIdentifer + "-" + this.identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafIdentifier that = (LeafIdentifier) o;

        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.identifier != that.identifier) return false;
        if (!this.pdbIdentifer.equals(that.pdbIdentifer)) return false;
        return this.chainIdentifer.equals(that.chainIdentifer);
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer.hashCode();
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + this.chainIdentifer.hashCode();
        result = 31 * result + this.identifier;
        return result;
    }
}
