package de.bioforscher.singa.chemistry.parser.pdb.structures.tokens;

import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.core.utility.Range;

import java.util.regex.Pattern;

import static de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.Justification.LEFT;
import static de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.Justification.RIGHT;

/**
 * @author cl
 */
public enum ChainTerminatorToken implements PDBToken{

    RECORD_TYPE(Range.of(1, 6), LEFT),
    ATOM_SERIAL(Range.of(7, 11), RIGHT),
    RESIDUE_NAME(Range.of(18, 20), RIGHT),
    CHAIN_IDENTIFIER(Range.of(22), LEFT),
    RESIDUE_SERIAL(Range.of(23, 26), RIGHT),
    RESIDUE_INSERTION(Range.of(27), LEFT);

    /**
     * A pattern describing all record names associated with this token structure. Use this to filter for lines that are
     * parsable with this token.
     */
    public static final Pattern RECORD_PATTERN = Pattern.compile("^TER.*");

    private final Range<Integer> columns;
    private final Justification justification;

    ChainTerminatorToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    /**
     * Creates a terminate record from the last leaf in the consecutive part of a chain.
     *
     * @param lastLeafOfChain The last leaf in the consecutive part of a chain.
     * @return The terminate record.
     */
    public static String assemblePDBLine(LeafSubstructure<?,?> lastLeafOfChain) {
        // TER     961      ASP A  62
        return "TER   " +
                String.format("%5d", (lastLeafOfChain.getAllAtoms().get(lastLeafOfChain.getAllAtoms().size() - 1).getIdentifier() + 1)) +
                "      " +
                lastLeafOfChain.getFamily().getThreeLetterCode().toUpperCase() +
                " " +
                lastLeafOfChain.getChainIdentifier() +
                String.format("%4d", lastLeafOfChain.getIdentifier().getSerial()) +
                lastLeafOfChain.getIdentifier().getInsertionCode();
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    @Override
    public Range<Integer> getColumns() {
        return this.columns;
    }

}
