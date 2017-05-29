package de.bioforscher.singa.units.parameters;

import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.units.UnitProvider.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

public final class EnvironmentalParameterExamples {

    public static void createFirstOrderReactionTestEnvironment() {
        EnvironmentalParameters.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));
        EnvironmentalParameters.getInstance().setSystemTemperature(EnvironmentalParameterDefaults.SYSTEM_TEMPERATURE);
        EnvironmentalParameters.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalParameters.getInstance().setCellularEnvironment(false);
    }

    public static void createEnzymeReactionTestEnvironment() {
        EnvironmentalParameters.getInstance().setNodeDistance(Quantities.getQuantity(500.0, NANO(METRE)));
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));
        EnvironmentalParameters.getInstance().setSystemTemperature(EnvironmentalParameterDefaults.SYSTEM_TEMPERATURE);
        EnvironmentalParameters.getInstance().setSystemViscosity(Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND)));
        EnvironmentalParameters.getInstance().setCellularEnvironment(false);
    }

}
