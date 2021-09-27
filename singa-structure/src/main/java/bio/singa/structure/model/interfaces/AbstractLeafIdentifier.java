package bio.singa.structure.model.interfaces;

import java.util.Objects;

public abstract class AbstractLeafIdentifier implements LeafIdentifier {

    private final String structureIdentifier;
    private final int modelIdentifier;
    private final String chainIdentifier;
    private final int serial;

    public AbstractLeafIdentifier(String structureIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        this.structureIdentifier = structureIdentifier;
        this.modelIdentifier = modelIdentifier;
        this.chainIdentifier = chainIdentifier;
        this.serial = serial;
    }

    public String getStructureIdentifier() {
        return structureIdentifier;
    }

    public int getModelIdentifier() {
        return modelIdentifier;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public int getSerial() {
        return serial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractLeafIdentifier that = (AbstractLeafIdentifier) o;

        if (modelIdentifier != that.modelIdentifier) return false;
        if (serial != that.serial) return false;
        if (!Objects.equals(structureIdentifier, that.structureIdentifier))
            return false;
        return Objects.equals(chainIdentifier, that.chainIdentifier);
    }

    @Override
    public int hashCode() {
        int result = structureIdentifier != null ? structureIdentifier.hashCode() : 0;
        result = 31 * result + modelIdentifier;
        result = 31 * result + (chainIdentifier != null ? chainIdentifier.hashCode() : 0);
        result = 31 * result + serial;
        return result;
    }
}
