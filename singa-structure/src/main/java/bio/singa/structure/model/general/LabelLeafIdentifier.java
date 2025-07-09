package bio.singa.structure.model.general;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.model.general.AuthLeafIdentifier.DEFAULT_INSERTION_CODE;
import static bio.singa.structure.model.general.AuthLeafIdentifier.DEFAULT_MODEL_IDENTIFIER;

/**
 * Addresses a {@link bio.singa.structure.model.interfaces.LeafSubstructure} using programmatically assigned label
 * identifiers. This style is only supported by the mmCIF file format.
 */
public class LabelLeafIdentifier extends AbstractLeafIdentifier {

    public static final String LABEL_IDENTIFIER_PREFIX = "LABEL";

    public LabelLeafIdentifier(String structureIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        super(structureIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
    }

    /**
     * Takes an array of leaf identifiers in simple string format (e.g. A-56) and returns {@link LabelLeafIdentifier}s.
     *
     * @param identifiers The identifiers in simple string format.
     * @return A list of {@link LabelLeafIdentifier}s.
     */
    public static List<LabelLeafIdentifier> of(String... identifiers) {
        return Arrays.stream(identifiers).map(LabelLeafIdentifier::fromSimpleString).collect(Collectors.toList());
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

    /**
     * Constructs a {@link LabelLeafIdentifier} from the given simple string. Only chain and residue number are
     * required.
     *
     * @param simpleString The identifier in string format (e.g. A-62 or A-62B).
     * @return The {@link LabelLeafIdentifier}.
     */
    public static LabelLeafIdentifier fromSimpleString(String simpleString) {
        String[] split = simpleString.split("-");
        String chainPart = split[0];
        String serialPart = split[1];
        return new LabelLeafIdentifier(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, chainPart, Integer.parseInt(serialPart));
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
