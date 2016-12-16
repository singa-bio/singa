package de.bioforscher.chemistry.physical.model;

import java.util.Comparator;

/**
 * Created by leberech on 16/12/16.
 */
public class LeafIdentifier implements Comparable<LeafIdentifier> {

    private static final Comparator<LeafIdentifier> comperator = Comparator
            .comparing(LeafIdentifier::getPdbIdentifer)
            .thenComparing(LeafIdentifier::getModelIdentifer)
            .thenComparing(LeafIdentifier::getChainIdentifer)
            .thenComparing(LeafIdentifier::getLeafIdentifer);

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
        this("0000", 0, chainIdentifer, leafIdentifer);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafIdentifier that = (LeafIdentifier) o;

        if (this.modelIdentifer != that.modelIdentifer) return false;
        if (this.leafIdentifer != that.leafIdentifer) return false;
        if (this.pdbIdentifer != null ? !this.pdbIdentifer.equals(that.pdbIdentifer) : that.pdbIdentifer != null)
            return false;
        return this.chainIdentifer != null ? this.chainIdentifer.equals(that.chainIdentifer) : that.chainIdentifer == null;
    }

    @Override
    public int hashCode() {
        int result = this.pdbIdentifer != null ? this.pdbIdentifer.hashCode() : 0;
        result = 31 * result + this.modelIdentifer;
        result = 31 * result + (this.chainIdentifer != null ? this.chainIdentifer.hashCode() : 0);
        result = 31 * result + this.leafIdentifer;
        return result;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return comperator.compare(this, o);
    }
}
