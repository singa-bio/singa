package bio.singa.structure.model.general;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import static bio.singa.structure.model.general.AuthLeafIdentifier.DEFAULT_INSERTION_CODE;

public class LabelLeafIdentifier extends AbstractLeafIdentifier {

    public static final String LABEL_IDENTIFIER_PREFIX = "LABEL";

    public LabelLeafIdentifier(String structureIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
    }

    /**
     * Constructs a {@link LabelLeafIdentifier} from its full string specification: structure identifier, model identifier,
     * chain identifier, serial number, and (optionally) insertion code.
     *
     * @param string The identifier in string format, with identifier specific prefix, e.g. CIF:1ZUH-1-1-A-62
     * @return The {@link LabelLeafIdentifier}.
     */
    public static LabelLeafIdentifier fromString(String string) {
        String[] split = string.split("-");
        if (split.length != 4) {
            throw new IllegalArgumentException("Label leaf identifiers can only contain 3 split characters (\"-\").");
        }
        String pdbIdentifier = split[0];
        int modelIdentifier = Integer.parseInt(split[1]);
        String chainIdentifier = split[2];
        int serial = Integer.parseInt(split[3]);
        return new LabelLeafIdentifier(pdbIdentifier, modelIdentifier, chainIdentifier, serial);
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
        return getStructureIdentifier() + "-" + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial();
    }


}
