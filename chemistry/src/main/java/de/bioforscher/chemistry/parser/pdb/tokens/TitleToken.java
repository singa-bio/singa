package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.core.utility.Range;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public enum TitleToken implements PDBToken {

    CLASSIFICATION(Range.of(11,50)),
    DEPOSITION_DATE(Range.of(51, 59)),
    ID_CODE(Range.of(63,66));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^HEADER.*");
    private final Range<Integer> columns;

    TitleToken(Range<Integer> columns) {
        this.columns = columns;
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
