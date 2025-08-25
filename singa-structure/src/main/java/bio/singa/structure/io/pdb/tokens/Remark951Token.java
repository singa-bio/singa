package bio.singa.structure.io.pdb.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.io.general.StructureRepresentationFactory;
import bio.singa.structure.model.general.AuthLeafIdentifier;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * SiNGA will use this remark to track 5-character residue serials encountered while parsing mmCIF content. This remark
 * provides the original identifier for downstream scripts.
 *
 * this should be compliant with pdb standards as long serials in a chain are don't exceed 9999
 *
 * Example
 * REMARK 951
 * REMARK 951 A1IYK renamed to LIG
 * REMARK 951
 *
 * @author sb
 */
public enum Remark951Token implements PDBToken {

    RECORD_TYPE(Range.of(1, 6)),
    REMARK_NUMBER(Range.of(8, 10)),
    REMARK_CONTENT(Range.of(12, 79));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_951 = Pattern.compile("^REMARK 951.*");
    private static final String PREFIX = "REMARK 951";
    public static final String SEPARATOR = " renamed to ";

    private final Range<Integer> columns;

    Remark951Token(Range<Integer> columns) {
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

    public static String assemblePDBLines(Map<AuthLeafIdentifier, Integer> mapping) {
        if (mapping == null || mapping.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        // REMARK 951 A1IYK renamed to LIG
        for (Map.Entry<AuthLeafIdentifier, Integer> entry : mapping.entrySet()) {
            String full = entry.getKey().toString();
            int lastDash = full.lastIndexOf('-');
            String replaced = full.substring(0, lastDash + 1) + entry.getValue();

            sb.append(PREFIX).append(" ")
                    .append(full)
                    .append(SEPARATOR)
                    .append(replaced)
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
