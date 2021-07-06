package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;

import java.util.regex.Pattern;

public enum Remark350Token implements PDBToken {

    RECORD_TYPE(Range.of(1, 6)),
    REMARK_NUMBER(Range.of(8, 10)),
    REMARK_CONTENT(Range.of(12, 79));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_350 = Pattern.compile("^REMARK 350.*");
    public static final Pattern REMARK_350_CHAINS = Pattern.compile("APPLY THE FOLLOWING TO CHAINS: (.*)");
    public static final Pattern REMARK_350_ID = Pattern.compile("BIOMOLECULE: (.*)");

    private final Range<Integer> columns;

    Remark350Token(Range<Integer> columns) {
        this.columns = columns;
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
