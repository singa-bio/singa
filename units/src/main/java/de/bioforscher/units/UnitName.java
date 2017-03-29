package de.bioforscher.units;

/**
 * The unit names required for SBML. This class provides a mapping between SBML and units of measurement.
 */
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
