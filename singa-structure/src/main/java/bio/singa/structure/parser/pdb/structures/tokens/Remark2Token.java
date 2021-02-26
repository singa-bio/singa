package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;

import java.util.regex.Pattern;

public enum Remark2Token implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    REMARK_NUMBER(Range.of(8, 10), Justification.RIGHT),
    REMARK_CONTENT(Range.of(24, 30), Justification.RIGHT);

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_2 = Pattern.compile("^REMARK   2.*");

    private final Range<Integer> columns;
    private final Justification justification;

    Remark2Token(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static String getContentOfRemark(String remarkLine) {
        return REMARK_CONTENT.extract(remarkLine);
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
