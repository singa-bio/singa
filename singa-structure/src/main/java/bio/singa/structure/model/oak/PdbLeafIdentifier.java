package bio.singa.structure.model.oak;

import bio.singa.structure.model.interfaces.AbstractLeafIdentifier;

import java.util.Comparator;

public class PdbLeafIdentifier extends AbstractLeafIdentifier implements Comparable<PdbLeafIdentifier> {

    public static final String DEFAULT_PDB_IDENTIFIER = "0000";
    public static final int DEFAULT_MODEL_IDENTIFIER = 1;
    public static final String DEFAULT_CHAIN_IDENTIFIER = "X";
    public static final char DEFAULT_INSERTION_CODE = '\u0000';
    public static final char DEFAULT_ALTERNATIVE_POSITION_CODE = '\u0000';

    public static final LeafIdentifier DEFAULT_LEAF_IDENTIFIER = new LeafIdentifier(1);

    private static final Comparator<PdbLeafIdentifier> leafIdentiferComparator = Comparator
            .comparing(PdbLeafIdentifier::getStructureIdentifier)
            .thenComparing(PdbLeafIdentifier::getModelIdentifier)
            .thenComparing(PdbLeafIdentifier::getChainIdentifier)
            .thenComparing(PdbLeafIdentifier::getSerial)
            .thenComparing(PdbLeafIdentifier::getInsertionCode);

    private final char insertionCode;

    public PdbLeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int serial, char insertionCode) {
        super(pdbIdentifer.toLowerCase(), modelIdentifer, chainIdentifer, serial);
        this.insertionCode = insertionCode;
    }

    public PdbLeafIdentifier(String pdbIdentifer, int modelIdentifer, String chainIdentifer, int serial) {
        this(pdbIdentifer, modelIdentifer, chainIdentifer, serial, DEFAULT_INSERTION_CODE);
    }

    /**
     * Constructs a {@link LeafIdentifier} from its full string specification: PDB-ID, model ID, chain ID, serial
     * number, and (optionally) insertion code.
     *
     * @param string The identifier in string format, e.g. 1ZUH-1-A-62
     * @return The {@link LeafIdentifier}.
     */
    public static LeafIdentifier fromString(String string) {
        String[] split = string.split("-");
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
            return new LeafIdentifier(pdbIdentifier, modelIdentifier, chainIdentifier, serial, insertionCode);
        } else {
            int serial = Integer.parseInt(serialPart);
            return new LeafIdentifier(pdbIdentifier, modelIdentifier, chainIdentifier, serial);
        }
    }

    /**
     * Constructs a {@link LeafIdentifier} from the given simple string. Only chain-ID, residue number and optional
     * insertion code is required.
     *
     * @param simpleString The identifier in string format (e.g. A-62 or A-62B).
     * @return The {@link LeafIdentifier}.
     */
    public static LeafIdentifier fromSimpleString(String simpleString) {
        String[] split = simpleString.split("-");
        // decide whether insertion code was specified
        String firstPart = split[0];
        String secondPart = split[1];
        if (secondPart.substring(secondPart.length() - 1).matches("[A-Z]")) {
            char insertionCode = secondPart.charAt(secondPart.length() - 1);
            return new LeafIdentifier(firstPart, Integer.parseInt(secondPart.substring(0, secondPart.length() - 1)), insertionCode);
        }
        return new LeafIdentifier(firstPart, Integer.parseInt(secondPart));
    }

    public char getInsertionCode() {
        return insertionCode;
    }

    @Override
    public int compareTo(PdbLeafIdentifier o) {
        return leafIdentiferComparator.compare(this, o);
    }

    @Override
    public String toString() {
        return getStructureIdentifier() + "-" + getModelIdentifier() + "-" + getChainIdentifier() + "-" + getSerial() + (insertionCode != DEFAULT_INSERTION_CODE ? insertionCode : "");
    }

    public String toSimpleString() {
        return getChainIdentifier() + "-" + getSerial() + (insertionCode != DEFAULT_INSERTION_CODE ? insertionCode : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PdbLeafIdentifier that = (PdbLeafIdentifier) o;

        return insertionCode == that.insertionCode;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) insertionCode;
        return result;
    }
}
