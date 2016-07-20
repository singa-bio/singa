package de.bioforscher.simulation.util;

import tec.units.ri.quantity.Quantities;

import static de.bioforscher.units.UnitDictionary.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

public final class EnvironmentFactory {

    public static void createFirstOrderReactionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);
    }

    public static void createEnzymeReactionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);
    }

}
