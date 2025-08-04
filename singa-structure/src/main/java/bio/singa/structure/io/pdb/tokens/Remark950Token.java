package bio.singa.structure.io.pdb.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.io.general.StructureRepresentationFactory;

import java.util.regex.Pattern;

/**
 * SiNGA will use this remark to track 5-character ligand identifiers encountered while parsing mmCIF content. This
 * remark provides the original identifier for downstream scripts.
 *
 * this should be compliant with pdb standards
 *
 * Example
 * REMARK 950
 * REMARK 950 A1IYK renamed to LIG
 * REMARK 950
 *
 * @author sb
 */
public enum Remark950Token implements PDBToken {

    RECORD_TYPE(Range.of(1, 6)),
    REMARK_NUMBER(Range.of(8, 10)),
    REMARK_CONTENT(Range.of(12, 79));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_950 = Pattern.compile("^REMARK 950.*");
    private static final String prefix = "REMARK 950";

    private final Range<Integer> columns;

    Remark950Token(Range<Integer> columns) {
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

    public static String assemblePDBLines(String threeLetterCode) {
        if (threeLetterCode == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        // REMARK 950 A1IYK renamed to LIG
        sb.append(prefix).append(" ").append(threeLetterCode).append(" renamed to ").append(StructureRepresentationFactory.LONG_LIGAND_NAME).append(System.lineSeparator());
        return sb.toString();
    }
}
