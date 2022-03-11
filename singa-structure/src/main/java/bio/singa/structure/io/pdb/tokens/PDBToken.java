package bio.singa.structure.io.pdb.tokens;

import bio.singa.core.utility.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tokens are used to extract information from PDB lines. Each token is identified by a static record pattern.
 * Afterwards the information from the line can be extracted.
 *
 * @author cl
 */
public interface PDBToken {

    int MAX_LINE_LENGTH = 69;

    /**
     * Extracts a value from a pdb line, given a token and line to extract from.
     *
     * @param line The line to extract from.
     * @param token The value for the token to extract.
     * @return The trimmed value.
     */
    static String extractValueFromPDBLine(String line, PDBToken token) {
        // pdb numbering starts at column 1 - string starts at 0 - therefore -1
        // pdb numbering is including the last letter  - substring is excluding the last letter this account for the
        // offset
        if (line.length() >= token.getColumns().getUpperBound()) {
            return line.substring(token.getColumns().getLowerBound() - 1, token.getColumns().getUpperBound()).trim();
        } else {
            if (line.length() >= token.getColumns().getLowerBound()) {
                return line.substring(token.getColumns().getLowerBound() - 1).trim();
            } else {
                return "";
            }
        }
    }

    static List<String> assembleLongLine(String prefix, String content) {
        List<String> lines = new ArrayList<>();
        // single line record
        if (content.length() < MAX_LINE_LENGTH) {
            lines.add(prefix + content);
        } else {
            while (content.length() >= MAX_LINE_LENGTH) {
                // if possible get last space before 70 char cutoff
                int endIndex = content.substring(0, MAX_LINE_LENGTH - 1).lastIndexOf(' ');
                String substring;
                if (endIndex <= 0) {
                    substring = content.substring(0, MAX_LINE_LENGTH - 1);
                } else {
                    substring = content.substring(0, endIndex);
                }
                lines.add(prefix + substring);
                content = content.substring(substring.length());
            }
            // last part
            lines.add(prefix + content);
        }
        return lines;
    }

    static String endLine(String content) {
        StringBuilder filler = new StringBuilder();
        for (int i = 0; i < 80 - content.length(); i++) {
            filler.append(" ");
        }
        return content + filler + System.lineSeparator();
    }

    /**
     * Returns the record name for this token.
     *
     * @return The record name for this token.
     */
    Pattern getRecordNamePattern();

    /**
     * Gets the columns (the staring and ending index) where a value can be extracted.
     *
     * @return The columns where a value can be extracted.
     */
    Range<Integer> getColumns();

    /**
     * Extracts a value from pdb line.
     *
     * @param line The line to extract from.
     * @return The trimmed value.
     */
    default String extract(String line) {
        return extractValueFromPDBLine(line, this);
    }


}
