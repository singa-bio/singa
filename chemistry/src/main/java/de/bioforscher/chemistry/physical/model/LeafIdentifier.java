package de.bioforscher.chemistry.physical.model;

import java.util.Comparator;

/**
 * Created by leberech on 16/12/16.
 */
public class LeafIdentifier implements Comparable<LeafIdentifier> {

    private static final Comparator<LeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(LeafIdentifier::getPdbIdentifer)
            .thenComparing(LeafIdentifier::getModelIdentifer)
            .thenComparing(LeafIdentifier::getChainIdentifer)
            .thenComparing(LeafIdentifier::getLeafIdentifer);

    public static final String DEFAULT_PDB_IDENTIFIER = "0000";
    public static final int DEFAULT_MODEL_IDENTIFIER = 0;
    public static final String DEFAULT_CHAIN_IDENTIFER = "X";

    private final String pdbIdentifer;
    private final int modelIdentifer;
    private final String chainIdentifer;
    private final int leafIdentifer;

    public LeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int leafIdentifer) {
        this.pdbIdentifer = pdbIdentifer.toUpperCase();
        this.modelIdentifer = modelIdentifer;
        this.chainIdentifer = chainIdentifer.toUpperCase();
        this.leafIdentifer = leafIdentifer;
    }

    public LeafIdentifier(String chainIdentifer, int leafIdentifer) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, chainIdentifer, leafIdentifer);
    }

    public LeafIdentifier(int leafIdentifer) {
        this(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, DEFAULT_CHAIN_IDENTIFER, leafIdentifer);
    }

    public static LeafIdentifier fromString(String identifier) {
        String[] split = identifier.split("-");
        return new LeafIdentifier(split[0], Integer.valueOf(split[1]));
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

    @Override
    public int compareTo(LeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
    }

    @Override
    public String toString() {
        return "LeafIdentifier{" +
                "pdbIdentifer='" + this.pdbIdentifer + '\'' +
                ", modelIdentifer=" + this.modelIdentifer +
                ", chainIdentifer='" + this.chainIdentifer + '\'' +
                ", leafIdentifer=" + this.leafIdentifer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafIdentifier that = (LeafIdentifier) o;

        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.leafIdentifer != that.leafIdentifer) return false;
        if (!this.pdbIdentifer.equals(that.pdbIdentifer)) return false;
        return this.chainIdentifer.equals(that.chainIdentifer);
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer.hashCode();
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + this.chainIdentifer.hashCode();
        result = 31 * result + this.leafIdentifer;
        return result;
    }
}
