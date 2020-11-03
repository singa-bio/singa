package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public enum TitleToken implements PDBToken {

    CONTINUATION(Range.of(9, 10)),
    TEXT(Range.of(11, 80));

    public static final Pattern RECORD_PATTERN = Pattern.compile("^TITLE.*");
    private final Range<Integer> columns;

    TitleToken(Range<Integer> columns) {
        this.columns = columns;
    }

    public static List<String> assemblePDBLines(String title) {
        title = title.toUpperCase();
        ArrayList<String> titleLines = new ArrayList<>();
        // single line record
        if (title.length() < 70) {
            titleLines.add("TITLE     " + title);
        } else {
            int continuation = 1;
            while (title.length() >= 70) {
                // get last space before 70 char cutoff
                if (continuation == 1) {
                    String substring = title.substring(0, title.substring(0, 70).lastIndexOf(' '));
                    titleLines.add("TITLE     " + substring);
                    title = title.substring(substring.length());
                } else {

                    int lastIndex = title.substring(0, 69).lastIndexOf(' ');
                    String substring;
                    if (lastIndex <= 0) {
                        substring = title.substring(0, 69);
                    } else {
                        substring = title.substring(0, lastIndex);
                    }
                    titleLines.add("TITLE    " + continuation + substring);
                    title = title.substring(substring.length());
                }
                continuation++;
            }
            // last part
            titleLines.add("TITLE    " + continuation + title);
        }
        return titleLines;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    @Override
    public String extract(String line) {
        if (line.length() >= getColumns().getUpperBound()) {
            return line.substring(getColumns().getLowerBound() - 1, getColumns().getUpperBound());
        } else {
            return line.substring(getColumns().getLowerBound() - 1);
        }
    }

}
