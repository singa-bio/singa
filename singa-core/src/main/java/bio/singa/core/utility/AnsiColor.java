package bio.singa.core.utility;

import java.util.StringJoiner;

/**
 * A class that allows printing to terminals in ANSI colors.
 */
public enum AnsiColor {

    HIGH_INTENSITY("\u001B[1m"),
    LOW_INTENSITY("\u001B[2m"),
    ITALIC("\u001B[3m"),
    UNDERLINE("\u001B[4m"),
    BLINK("\u001B[5m"),
    RAPID_BLINK("\u001B[6m"),
    REVERSE_VIDEO("\u001B[7m"),
    INVISIBLE_TEXT("\u001B[8m"),
    ANSI_RESET("\u001B[0m"),
    ANSI_BLACK("\u001B[30m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m"),
    ANSI_BRIGHT_BLACK("\u001B[90m"),
    ANSI_BRIGHT_RED("\u001B[91m"),
    ANSI_BRIGHT_GREEN("\u001B[92m"),
    ANSI_BRIGHT_YELLOW("\u001B[93m"),
    ANSI_BRIGHT_BLUE("\u001B[94m"),
    ANSI_BRIGHT_PURPLE("\u001B[95m"),
    ANSI_BRIGHT_CYAN("\u001B[96m"),
    ANSI_BRIGHT_WHITE("\u001B[97m"),
    BG_BLACK("\u001B[40m"),
    BG_RED("\u001B[41m"),
    BG_GREEN("\u001B[42m"),
    BG_YELLOW("\u001B[43m"),
    BG_BLUE("\u001B[44m"),
    BG_PURPLE("\u001B[45m"),
    BG_CYAN("\u001B[46m"),
    BG_WHITE("\u001B[47m"),
    BG_BRIGHT_BLACK("\u001B[100m"),
    BG_BRIGHT_RED("\u001B[101m"),
    BG_BRIGHT_GREEN("\u001B[102m"),
    BG_BRIGHT_YELLOW("\u001B[103m"),
    BG_BRIGHT_BLUE("\u001B[104m"),
    BG_BRIGHT_PURPLE("\u001B[105m"),
    BG_BRIGHT_CYAN("\u001B[106m"),
    BG_BRIGHT_WHITE("\u001B[107m");

    private final String colorCode;

    AnsiColor(String colorCode) {
        this.colorCode = colorCode;
    }

    public static String colorize(Object object, AnsiColor... ansiColors) {
        StringJoiner stringJoiner = new StringJoiner("");
        for (AnsiColor ansiColor : ansiColors) {
            stringJoiner.add(ansiColor.colorCode);
        }
        stringJoiner.add(object.toString());
        stringJoiner.add(ANSI_RESET.colorCode);
        return stringJoiner.toString();
    }

    public String getColorCode() {
        return colorCode;
    }
}
