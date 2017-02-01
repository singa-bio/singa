package de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.quantity.Dimensionless;

/**
 * @author leberech
 */
public enum KineticParameterType {

    HILL_COEFFICIENT("Hill coefficient", "N", Dimensionless.class),
    MICHAELIS_CONSTANT("Michaelis constant", "KM", MolarConcentration.class),
    INHIBITORY_CONSTANT("Inhibitory constant", "KI", MolarConcentration.class),
    CATALYTIC_CONSTANT("Catalytic constant", "KCAT", ReactionRate.class),
    MAXIMAL_VELOCITY("Maximal velocity", "VMAX", ReactionRate.class);

    private final String name;
    private final String symbol;
    private final Class<?> quantity;

    KineticParameterType(String name, String symbol, Class<?> quantity) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Class<?> getQuantity() {
        return this.quantity;
    }
}
