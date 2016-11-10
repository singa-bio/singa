package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.core.utility.Range;

import java.util.regex.Pattern;

/**
 * Created by Christoph on 09/11/2016.
 */
public enum ModelToken implements PDBToken {

    MODEL_SERIAL(Range.of(11, 14));

    /**
     * A pattern describing all record names associated with this token structure. Use this to filter for lines that are
     * parsable with this token.
     */
    public static final Pattern RECORD_PATTERN = Pattern.compile("^MODEL .*");

    private final Range<Integer> columns;

    ModelToken(Range columns) {
        this.columns = columns;
    }

    @Override
    public Range<Integer> getColumns() {
        return this.columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

}
