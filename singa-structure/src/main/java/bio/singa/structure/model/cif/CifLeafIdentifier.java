package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import static bio.singa.structure.model.oak.PdbLeafIdentifier.DEFAULT_INSERTION_CODE;

public class CifLeafIdentifier extends AbstractLeafIdentifier {

    public int entityIdentifier;

    public CifLeafIdentifier(String structureIdentifier, int entityIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier, modelIdentifier, chainIdentifier, serial);
        this.entityIdentifier = entityIdentifier;
    }

    public int getEntityIdentifier() {
        return entityIdentifier;
    }

    @Override
    public boolean hasInsertionCode() {
        return false;
    }

    @Override
    public char getInsertionCode() {
        return DEFAULT_INSERTION_CODE;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return LEAF_IDENTIFIER_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return getStructureIdentifier() + "-" + entityIdentifier + "-" + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CifLeafIdentifier that = (CifLeafIdentifier) o;

        return entityIdentifier == that.entityIdentifier;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + entityIdentifier;
        return result;
    }

}
