package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import java.util.Locale;

import static bio.singa.structure.model.pdb.PdbLeafIdentifier.DEFAULT_INSERTION_CODE;

public class CifLeafIdentifier extends AbstractLeafIdentifier {

    private static final int DEFAULT_ENTITY_IDENTIFIER = 0;
    public int entityIdentifier;

    public CifLeafIdentifier(String structureIdentifier, int entityIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
        this.entityIdentifier = entityIdentifier;
    }

    public CifLeafIdentifier(String structureIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
        entityIdentifier = DEFAULT_ENTITY_IDENTIFIER;
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
        return getStructureIdentifier() + "-" + (entityIdentifier != DEFAULT_ENTITY_IDENTIFIER ? entityIdentifier+"-" : "") + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial();
    }



}
