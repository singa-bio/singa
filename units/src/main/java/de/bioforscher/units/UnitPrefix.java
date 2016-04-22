package de.bioforscher.units;

public enum UnitPrefix {

    TERA(12, "T"),
    GIGA(9, "G"),
    MEGA(6, "M"),
    KILO(3, "k"),
    HECTO(2, "h"),
    DECA(1, "da"),
    DECI(-1, "d"),
    CENTI(-2, "c"),
    MILI(-3, "m"),
    MICRO(-6, "\u00B5"), // maybe use "u" instead to prevent ISO-8859-1 encoding
    NANO(-9, "n"),
    PICO(-12, "p"),
    FEMTO(-15, "f"),
    NO_PREFIX(0, "");

    private final int scale;
    private final String symbol;

    private UnitPrefix(int scale, String symbol) {
        this.scale = scale;
        this.symbol = symbol;
    }

    public int getScale() {
        return this.scale;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
