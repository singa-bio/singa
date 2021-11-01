package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;

import java.util.Locale;

import static bio.singa.structure.model.pdb.PdbLeafIdentifier.DEFAULT_INSERTION_CODE;

public class CifLeafIdentifier extends AbstractLeafIdentifier {

    public static final String CIF_IDENTIFIER_PREFIX = "CIF";
    public static final int DEFAULT_ENTITY_IDENTIFIER = 0;

    public int entityIdentifier;

    public CifLeafIdentifier(String structureIdentifier, int entityIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
        this.entityIdentifier = entityIdentifier;
    }

    public CifLeafIdentifier(String structureIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
        entityIdentifier = DEFAULT_ENTITY_IDENTIFIER;
    }

    /**
     * Constructs a {@link CifLeafIdentifier} from its full string specification: structure identifier, model identifier,
     * chain identifier, serial number, and (optionally) insertion code.
     *
     * @param string The identifier in string format, with identifier specific prefix, e.g. CIF:1ZUH-1-1-A-62
     * @return The {@link CifLeafIdentifier}.
     */
    public static CifLeafIdentifier fromString(String string) {
        if (!string.startsWith("CIF")) {
            throw new IllegalArgumentException("CIF leaf identifiers must start with the CIF prefix (e.g. CIF:1ZUH-1-1-A-62) if parsed from raw string.");
        }
        string = string.substring(4);
        String[] split = string.split("-");
        if (split.length != 5) {
            throw new IllegalArgumentException("PDB leaf identifiers can only contain 4 split characters (\"-\").");
        }
        String pdbIdentifier = split[0];
        int modelIdentifier = Integer.parseInt(split[1]);
        int entityIdentifier = Integer.parseInt(split[2]);
        String chainIdentifier = split[3];
        int serial = Integer.parseInt(split[4]);
        return new CifLeafIdentifier(pdbIdentifier, entityIdentifier, modelIdentifier, chainIdentifier, serial);
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
        return CIF_IDENTIFIER_PREFIX + ":" + getStructureIdentifier() + "-" + (entityIdentifier != DEFAULT_ENTITY_IDENTIFIER ? entityIdentifier + "-" : "") + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial();
    }


}
