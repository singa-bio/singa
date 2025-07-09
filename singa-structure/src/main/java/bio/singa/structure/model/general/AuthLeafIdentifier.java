package bio.singa.structure.model.general;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AuthLeafIdentifier extends AbstractLeafIdentifier {

    public static final String AUTH_IDENTIFIER_PREFIX = "AUTH";
    public static final int DEFAULT_MODEL_IDENTIFIER = 1;
    public static final String DEFAULT_CHAIN_IDENTIFIER = "X";
    public static final char DEFAULT_INSERTION_CODE = '\u0000';
    public static final char DEFAULT_ALTERNATIVE_POSITION_CODE = '\u0000';

    public static final AuthLeafIdentifier DEFAULT_LEAF_IDENTIFIER = new AuthLeafIdentifier(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, DEFAULT_CHAIN_IDENTIFIER, 1);

    private final char insertionCode;

    public AuthLeafIdentifier(String pdbIdentifier, int modelIdentifier, String chainIdentifier, int serial, char insertionCode) {
        super(pdbIdentifier.toLowerCase(), modelIdentifier, chainIdentifier, serial);
        this.insertionCode = insertionCode;
    }

    public AuthLeafIdentifier(String pdbIdentifier, int modelIdentifier, String chainIdentifier, int serial) {
        this(pdbIdentifier, modelIdentifier, chainIdentifier, serial, DEFAULT_INSERTION_CODE);
    }

    /**
     * Takes an array of leaf identifiers in simple string format (e.g. A-56) and returns {@link AuthLeafIdentifier}s.
     *
     * @param identifiers The identifiers in simple string format.
     * @return A list of {@link AuthLeafIdentifier}s.
     */
    public static List<AuthLeafIdentifier> of(String... identifiers) {
        return Arrays.stream(identifiers).map(AuthLeafIdentifier::fromSimpleString).collect(Collectors.toList());
    }

    /**
     * Constructs a {@link AuthLeafIdentifier} from its full string specification: structure identifier, model identifier,
     * chain identifier, serial number, and (optionally) insertion code.
     *
     * @param string The identifier in string format, with identifier specific prefix, e.g. PDB:1ZUH-1-A-62
     * @return The {@link AuthLeafIdentifier}.
     */
    public static AuthLeafIdentifier fromString(String string) {
        String[] split = string.split("-");
        if (split.length < 4 || split.length > 5) {
            throw new IllegalArgumentException("Auth leaf identifiers can only contain 3 or 4 (in case of negative serials) split characters (\"-\").");
        }
        String pdbIdentifier = split[0];
        int modelIdentifier = Integer.parseInt(split[1]);
        String chainIdentifier = split[2];
        // decide whether insertion code was used
        String serialPart = split[3];
        // negative serials
        if (serialPart.isEmpty()) {
            serialPart = "-" + split[4];
        }
        if (serialPart.substring(serialPart.length() - 1).matches("[A-Za-z]")) {
            char insertionCode = serialPart.charAt(serialPart.length() - 1);
            int serial = Integer.parseInt(serialPart.substring(0, serialPart.length() - 1));
            return new AuthLeafIdentifier(pdbIdentifier, modelIdentifier, chainIdentifier, serial, insertionCode);
        } else {
            int serial = Integer.parseInt(serialPart);
            return new AuthLeafIdentifier(pdbIdentifier, modelIdentifier, chainIdentifier, serial);
        }
    }

    /**
     * Constructs a {@link AuthLeafIdentifier} from the given simple string. Only chain, residue number and optional
     * insertion code is required.
     *
     * @param simpleString The identifier in string format (e.g. A-62 or A-62B).
     * @return The {@link AuthLeafIdentifier}.
     */
    public static AuthLeafIdentifier fromSimpleString(String simpleString) {
        String[] split = simpleString.split("-");
        // decide whether insertion code was specified
        String firstPart = split[0];
        String secondPart = split[1];
        if (secondPart.substring(secondPart.length() - 1).matches("[A-Z]")) {
            char insertionCode = secondPart.charAt(secondPart.length() - 1);
            return new AuthLeafIdentifier(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, firstPart, Integer.parseInt(secondPart.substring(0, secondPart.length() - 1)), insertionCode);
        }
        return new AuthLeafIdentifier(DEFAULT_PDB_IDENTIFIER, DEFAULT_MODEL_IDENTIFIER, firstPart, Integer.parseInt(secondPart));
    }

    public char getInsertionCode() {
        return insertionCode;
    }

    @Override
    public boolean hasInsertionCode() {
        return insertionCode != DEFAULT_INSERTION_CODE;
    }

    @Override
    public int compareTo(LeafIdentifier o) {
        return LEAF_IDENTIFIER_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return getStructureIdentifier() + "-" + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial() + (insertionCode != DEFAULT_INSERTION_CODE ? insertionCode : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthLeafIdentifier that = (AuthLeafIdentifier) o;

        return insertionCode == that.insertionCode;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) insertionCode;
        return result;
    }
}
