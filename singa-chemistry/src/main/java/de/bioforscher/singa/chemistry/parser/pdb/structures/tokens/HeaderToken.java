package de.bioforscher.singa.chemistry.parser.pdb.structures.tokens;

import de.bioforscher.singa.core.utility.Range;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public enum HeaderToken implements PDBToken {

    CLASSIFICATION(Range.of(11, 50)),
    DEPOSITION_DATE(Range.of(51, 59)),
    ID_CODE(Range.of(63, 66));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^HEADER.*");
    private final Range<Integer> columns;

    HeaderToken(Range<Integer> columns) {
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

    public String createHeaderLine(String content) {
        return "HEADER" + String.format("%" + (getColumns().getLowerBound() - content.length() + 1) + "s", content.toUpperCase());
    }
}
