package bio.singa.simulation.export;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class TeXFormat {

    private static Pattern doublePattern = Pattern.compile("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    public static String COLUMN_SEPERATOR = "&";
    public static String COLUMN_SEPERATOR_SPACED = " & ";
    public static String COLUMN_END_NON_BREAKING = "\\\\*\n";
    public static String COLUMN_END_BREAKING = "\\\\\n";

    public static String setMonoSpace(String string) {
        return "\\texttt{" + string + "}";
    }

    public static String setCursive(String string) {
        return "\\textit{" + string + "}";
    }

    public static String replaceTextMu(String string) {
        return string.replace("µ", "\\textmu ");
    }

    public static String formatTableNumberColumnValue(String string) {
        if (doublePattern.matcher(string).matches()) {
            double value = Double.parseDouble(string);
            string = String.format("%6.3e", value);
            string = string.replace("E", "e");
            return string;
        }
        return "{" + string + "}";
    }

    public static String formatTableMultiColumn(String content, int span) {
        return "\\multicolumn{" + span + "}{l}{" + content + "}";
    }

}
