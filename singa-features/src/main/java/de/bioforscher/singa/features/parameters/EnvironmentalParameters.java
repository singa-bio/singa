package de.bioforscher.singa.features.parameters;

import de.bioforscher.singa.features.model.QuantityFormatter;
import de.bioforscher.singa.features.quantities.DynamicViscosity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.function.MultiplyConverter;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.TransformedUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.features.units.UnitProvider.PASCAL_SECOND;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

public class EnvironmentalParameters extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentalParameters.class);

    public static final QuantityFormatter<Time> TIME_FORMATTER = new QuantityFormatter<>(SECOND, true);

    private static final DecimalFormat DELTA_VALUE_FORMATTER = new DecimalFormat("0.####E00");
    public static final QuantityFormatter<MolarConcentration> DELTA_FORMATTER = new QuantityFormatter<>(DELTA_VALUE_FORMATTER, MOLE_PER_LITRE, false);

    /**
     * Standard node distance [length] (100 nm)
     */
    public static final Quantity<Length> DEFAULT_NODE_DISTANCE = Quantities.getQuantity(100.0, NANO(METRE));

    /**
     * Standard time step size [time] (1 us)
     */
    public static final Quantity<Time> DEFAULT_TIME_STEP = Quantities.getQuantity(10.0, MICRO(SECOND));

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    public static final Quantity<Temperature> DEFAULT_TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity of Water at 20 C)
     */
    public static final Quantity<DynamicViscosity> DEFAULT_VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    /**
     * The molar mass of water.
     */
    public static final double MOLAR_MASS_OF_WATER = 18.0153;

    private static EnvironmentalParameters instance;

    private Quantity<Length> nodeDistance;
    private Quantity<Time> timeStep;
    private Quantity<Temperature> systemTemperature;
    private Quantity<DynamicViscosity> systemViscosity;

    private Unit<MolarConcentration> transformedMolarConcentration = MOLE_PER_LITRE;
    private Quantity<MolarConcentration> emptyConcentration = Quantities.getQuantity(0.0, MOLE_PER_LITRE);

    private Unit<Volume> transformedVolume = CUBIC_METRE;
    private Unit<Area> transformedArea = SQUARE_METRE;
    private Unit<Length> transformedLength = METRE;


    private EnvironmentalParameters() {
        nodeDistance = DEFAULT_NODE_DISTANCE;
        timeStep = DEFAULT_TIME_STEP;
        systemTemperature = DEFAULT_TEMPERATURE;
        systemViscosity = DEFAULT_VISCOSITY;
    }

    private static EnvironmentalParameters getInstance() {
        if (instance == null) {
            synchronized (EnvironmentalParameters.class) {
                instance = new EnvironmentalParameters();
            }
        }
        return instance;
    }

    public static Quantity<Length> getNodeDistance() {
        return instance.nodeDistance;
    }

    public static void setNodeDistance(Quantity<Length> nodeDistance) {
        logger.debug("Setting node distance to {}.", nodeDistance);
        getInstance().nodeDistance = nodeDistance;
        getInstance().setTransformedMolarConcentrationUnit();
        getInstance().transformSpaceScales();
        getInstance().emptyConcentration = Quantities.getQuantity(0.0, getTransformedMolarConcentration());
        getInstance().setChanged();
        getInstance().notifyObservers();
    }

    public static Quantity<MolarConcentration> emptyConcentration() {
        return getInstance().emptyConcentration;
    }

    private void setTransformedMolarConcentrationUnit() {
        final Unit<Length> nodeDistanceUnit = nodeDistance.getUnit();
        final Unit<MolarConcentration> transformedUnit = MOLE.divide(nodeDistanceUnit.pow(3)).asType(MolarConcentration.class);
        if (nodeDistance.getValue().doubleValue() == 1.0) {
            transformedMolarConcentration = transformedUnit;
        } else {
            transformedMolarConcentration = new TransformedUnit<>(transformedUnit, new MultiplyConverter(Math.pow(nodeDistance.getValue().doubleValue(), 3)));
        }
    }

    public static Unit<MolarConcentration> getTransformedMolarConcentration() {
        return getInstance().transformedMolarConcentration;
    }

    public void transformSpaceScales() {
        // base length unit
        final Unit<Length> lengthUnit = nodeDistance.getUnit();
        // base area unit
        final Unit<Area> areaUnit = lengthUnit.pow(2).asType(Area.class);
        // base volume unit
        final Unit<Volume> volumeUnit = lengthUnit.pow(3).asType(Volume.class);
        // transform with multiplier if necessary
        if (nodeDistance.getValue().doubleValue() == 1.0) {
            transformedLength = lengthUnit;
            transformedArea = areaUnit;
            transformedVolume = volumeUnit;
        } else {
            transformedLength = new TransformedUnit<>(lengthUnit, new MultiplyConverter(nodeDistance.getValue().doubleValue()));
            transformedArea = new TransformedUnit<>(areaUnit, new MultiplyConverter(Math.pow(nodeDistance.getValue().doubleValue(), 2)));
            transformedVolume = new TransformedUnit<>(volumeUnit, new MultiplyConverter(Math.pow(nodeDistance.getValue().doubleValue(), 3)));
        }
    }

    public static Unit<Area> getTransformedArea() {
        return getInstance().transformedArea;
    }

    public static Unit<Length> getTransformedLength() {
        return getInstance().transformedLength;
    }

    public static Unit<Volume> getTransformedVolume() {
        return getInstance().transformedVolume;
    }

    public static Quantity<Temperature> getTemperature() {
        return getInstance().systemTemperature;
    }

    public static void setTemperature(Quantity<Temperature> temperature) {
        logger.debug("Setting environmental temperature to {}.", temperature);
        getInstance().systemTemperature = temperature.to(KELVIN);
        getInstance().setChanged();
        getInstance().notifyObservers();
    }

    public static Quantity<DynamicViscosity> getViscosity() {
        return getInstance().systemViscosity;
    }

    public static void setSystemViscosity(Quantity<DynamicViscosity> viscosity) {
        logger.debug("Setting environmental dynamic viscosity of to {}.", viscosity);
        getInstance().systemViscosity = viscosity.to(MILLI(PASCAL_SECOND));
        getInstance().setChanged();
        getInstance().notifyObservers();
    }

    public static Quantity<Time> getTimeStep() {
        return getInstance().timeStep;
    }

    public static void setTimeStep(Quantity<Time> timeStep) {
        logger.trace("Setting time step size to {}.", timeStep);
        getInstance().timeStep = timeStep;
        // getInstance().setChanged();
        // getInstance().notifyObservers();
    }

    public static void setNodeSpacingToDiameter(Quantity<Length> diameter, int spanningNodes) {
        logger.debug("Setting system diameter to {} using {} spanning nodes.", diameter, spanningNodes);
        setNodeDistance(diameter.divide(spanningNodes - 1));
    }

    public static void attachObserver(Observer observer) {
        getInstance().addObserver(observer);
    }

}
