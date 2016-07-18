package de.bioforscher.simulation.util;

import tec.units.ri.quantity.Quantities;

import static de.bioforscher.units.UnitDictionary.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

public final class EnvironmentFactory {

    public static void createFirstOrderReactionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);
    }

    public static void createSecondOrderReactionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, SECOND));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);
    }

    public static void createNthOrderReactionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));
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

    public static void createBioDiffusionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(5.0, MICRO(SECOND)));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(true);
    }

    public static void createSmallDiffusionTestEnvironment() {
        EnvironmentalVariables.getInstance().setNodeDistance(Quantities.getQuantity(250.0, NANO(METRE)));
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, MICRO(SECOND)));
        EnvironmentalVariables.getInstance().setSystemTemperature(SystemDefaultConstants.SYSTEM_TEMPERATURE);
        EnvironmentalVariables.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);
    }
}
