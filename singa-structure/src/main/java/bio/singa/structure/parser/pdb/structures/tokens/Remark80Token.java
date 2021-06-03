package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;

import java.util.regex.Pattern;

/**
 * SiNGA will use this remark to assign properties to ligands
 * first line is empty
 * second line is the referenced ligand (three letter code)
 * third line is the property type
 * following lines are the value of the property
 * empty line signifies end of one attribute
 *
 * this should be compliant whith pdb standards
 *
 * Example
 * REMARK  80
 * REMARK  80 PEP
 * REMARK  80 INCHI
 * REMARK  80 InChI=1S/C3H5O6P/c1-2(3(4)5)9-10(6,7)8/h1H2,(H,4,5)(H2,6,7,8)
 * REMARK  80
 *
 * @author cl
 */
public enum Remark80Token implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    REMARK_NUMBER(Range.of(8, 10), Justification.RIGHT),
    REMARK_CONTENT(Range.of(12, 79), Justification.LEFT);

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_80 = Pattern.compile("^REMARK  80.*");

    private final Range<Integer> columns;
    private final Justification justification;

    Remark80Token(Range<Integer> columns, Justification justification) {
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
