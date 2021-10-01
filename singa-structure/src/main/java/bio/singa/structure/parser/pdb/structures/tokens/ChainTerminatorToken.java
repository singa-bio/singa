package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;

import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public enum ChainTerminatorToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    ATOM_SERIAL(Range.of(7, 11), Justification.RIGHT),
    RESIDUE_NAME(Range.of(18, 20), Justification.RIGHT),
    CHAIN_IDENTIFIER(Range.of(22), Justification.LEFT),
    RESIDUE_SERIAL(Range.of(23, 26), Justification.RIGHT),
    RESIDUE_INSERTION(Range.of(27), Justification.LEFT);

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
    public static String assemblePDBLine(LeafSubstructure lastLeafOfChain) {
        Integer atomIdentifier = lastLeafOfChain.getAllAtoms().stream()
                .sorted(Comparator.comparingInt(Atom::getAtomIdentifier))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("unable to get last atom of leaf " + lastLeafOfChain))
                .getAtomIdentifier();
        // TER     961      ASP A  62
        return "TER   " +
                String.format("%5d", atomIdentifier) +
                "      " +
                RESIDUE_NAME.createTokenString(lastLeafOfChain.getFamily().getThreeLetterCode()) +
                " " +
                lastLeafOfChain.getIdentifier().getChainIdentifier() +
                String.format("%4d", lastLeafOfChain.getIdentifier().getSerial()) +
                (!lastLeafOfChain.getIdentifier().hasInsertionCode() ? "" : lastLeafOfChain.getIdentifier().getInsertionCode());
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    private String createTokenString(String content) {
        int totalLength = columns.getUpperBound() - columns.getLowerBound() - content.length();
        StringBuilder filler = new StringBuilder();
        for (int i = 0; i < totalLength + 1; i++) {
            filler.append(" ");
        }
        if (justification == Justification.LEFT) {
            return content + filler;
        }
        return filler + content;
    }

}
