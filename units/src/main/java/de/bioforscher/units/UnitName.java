package de.bioforscher.units;

public enum UnitName {

    METRE("m"),
    GRAM("g"),
    SECOND("s"),
    KELVIN("K"),
    CELSIUS("C"),
    MOLE("mol"),
    PASCAL("Pa");

    private final String symbol;

    private UnitName(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
