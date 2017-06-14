package de.bioforscher.singa.features.parameters;

import de.bioforscher.singa.features.quantities.DynamicViscosity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.features.units.UnitProvider.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.*;

public final class EnvironmentalParameterDefaults {

    /**
     * Standard node distance [length] (100 nm)
     */
    public static final Quantity<Length> NODE_DISTANCE = Quantities.getQuantity(100.0, NANO(METRE));

    /**
     * Standard time step size [time] (1 us)
     */
    public static final Quantity<Time> TIME_STEP = Quantities.getQuantity(10.0, MICRO(SECOND));

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    public static final Quantity<Temperature> SYSTEM_TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity
     * of Water at 20 C)
     */
    public static final Quantity<DynamicViscosity> SYSTEM_VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    /**
     * The molar mass of water.
     */
    public static final double MOLAR_MASS_OF_WATER = 18.0153;



}
