package de.bioforscher.units;

public enum UnitName {

    METRE("m"),
    GRAM("g"),
    SECOND("s"),
    KELVIN("K"),
    CELSIUS("\u00BAC"),
    MOLE("mol"),
    PASCAL("Pa");

    private final String symbol;

    UnitName(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
