package bio.singa.core.utility;

import java.util.regex.Pattern;

public class DoubleMatcher {

    private static Pattern doublePattern = Pattern.compile("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    public static boolean containsDouble(String string) {
        return doublePattern.matcher(string).matches();
    }

}
