package bio.singa.structure.io.pdb.tokens;

import bio.singa.core.utility.Range;

import java.util.regex.Pattern;

public enum ModelToken implements PDBToken {

    MODEL_SERIAL(Range.of(11, 14));

    /**
     * A pattern describing all record names associated with this token structure. Use this to filter for lines that are
     * parsable with this token.
     */
    public static final Pattern RECORD_PATTERN = Pattern.compile("^MODEL .*");

    private final Range<Integer> columns;

    ModelToken(Range<Integer> columns) {
        this.columns = columns;
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

}
