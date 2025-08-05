package bio.singa.structure.io.pdb.tokens;

import bio.singa.core.utility.Range;

import java.util.List;
import java.util.regex.Pattern;

/**
 * SiNGA will use this remark to assign properties to ligands
 * first line is empty
 * second line is the referenced ligand (three letter code)
 * third line is the property type
 * following lines are the value of the property
 * empty line signifies end of one attribute
 *
 * this should be compliant with pdb standards
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

    RECORD_TYPE(Range.of(1, 6)),
    REMARK_NUMBER(Range.of(8, 10)),
    REMARK_CONTENT(Range.of(12, 79));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(REMARK).*");
    public static final Pattern REMARK_80 = Pattern.compile("^REMARK  80.*");
    private static final String prefix = "REMARK  80 ";

    private final Range<Integer> columns;

    Remark80Token(Range<Integer> columns) {
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

    public static String assemblePDBLines(String threeLetterCode, String inchi) {
        if (threeLetterCode == null || inchi == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("REMARK  80").append(System.lineSeparator());
        sb.append("REMARK  80 ").append(threeLetterCode).append(System.lineSeparator());
        sb.append("REMARK  80 ").append("INCHI").append(System.lineSeparator());
        List<String> longLine = PDBToken.assembleLongLine(prefix, inchi);
        longLine.forEach(line -> sb.append(line).append(System.lineSeparator()));
        return sb.toString();
    }

    public static void main(String[] args) {
        String lines = assemblePDBLines("P_1", "InChI=1S/C75H26N25O23S/c1-14-39(10)59(100-68(118)49(30-55(108)109)96-67(117)48(29-51(78)103)95-63(113)45(21-18-25-84-75(81)82)92-64(114)46(22-26-124-13)93-66(116)47(27-35(2)3)94-62(112)43(77)28-54(106)107)70(120)86-32-52(104)90-44(20-17-24-83-74(79)80)65(115)98-57(37(6)7)72(122)88-41(12)61(111)97-56(36(4)5)71(121)87-40(11)60(110)85-31-53(105)91-50(34-102)69(119)99-58(38(8)9)73(123)89-42(33-101)19-15-16-23-76/h1-13H2");
        System.out.println(lines);
    }



}
