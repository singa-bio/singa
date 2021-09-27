package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;

import java.util.Comparator;

public class CifLeafIdentifier extends AbstractLeafIdentifier implements Comparable<CifLeafIdentifier> {

    public int entityIdentifier;

    private static final Comparator<CifLeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(CifLeafIdentifier::getStructureIdentifier)
            .thenComparing(CifLeafIdentifier::getEntityIdentifier)
            .thenComparing(CifLeafIdentifier::getModelIdentifier)
            .thenComparing(CifLeafIdentifier::getChainIdentifier)
            .thenComparing(CifLeafIdentifier::getSerial);

    public CifLeafIdentifier(String structureIdentifier, int entityIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier, modelIdentifier, chainIdentifier, serial);
        this.entityIdentifier = entityIdentifier;
    }

    public int getEntityIdentifier() {
        return entityIdentifier;
    }

    @Override
    public int compareTo(CifLeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
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
