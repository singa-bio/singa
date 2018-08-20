package bio.singa.features.parameters;

import bio.singa.features.model.QuantityFormatter;
import bio.singa.features.quantities.DynamicViscosity;
import bio.singa.features.quantities.MolarConcentration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.PASCAL_SECOND;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.*;

public class Environment extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    public static final QuantityFormatter<Time> TIME_FORMATTER = new QuantityFormatter<>(SECOND, true);

    private static final DecimalFormat DELTA_VALUE_FORMATTER = new DecimalFormat("0.####E00");
    public static final QuantityFormatter<MolarConcentration> DELTA_FORMATTER = new QuantityFormatter<>(DELTA_VALUE_FORMATTER, MOLE_PER_LITRE, false);

    /**
     * Standard node distance [length] (100 nm)
     */
    public static final Quantity<Length> DEFAULT_NODE_DISTANCE = Quantities.getQuantity(1.0, MICRO(METRE));

    /**
     * Standard time step size [time] (1 us)
     */
    public static final Quantity<Time> DEFAULT_TIME_STEP = Quantities.getQuantity(1.0, MICRO(SECOND));

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    public static final Quantity<Temperature> DEFAULT_TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity of Water at 20 C)
     */
    public static final Quantity<DynamicViscosity> DEFAULT_VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    /**
     * Standard system extend [length] (5 um)
     */
    public static final Quantity<Length> DEFAULT_SYSTEM_EXTEND = Quantities.getQuantity(1.0, MICRO(METRE));

    /**
     * Standard simulation extend [pseudo length] 500
     */
    public static final double DEFAULT_SIMULATION_EXTEND = 100;

    /**
     * The singleton instance.
     */
    private static Environment instance;


    /**
     * The global temperature of the simulation system.
     */
    private Quantity<Temperature> systemTemperature;

    /**
     * The global viscosity of the simulation system.
     */
    private Quantity<DynamicViscosity> systemViscosity;

    /**
     * An empty concentration Quantity
     */
    private Quantity<MolarConcentration> emptyConcentration = Quantities.getQuantity(0.0, MOLE_PER_LITRE);

    /**
     * The current time between two simulation epochs. Called time step.
     */
    private Quantity<Time> timeStep;

    /**
     * The distance between two nodes (the side length of a subsection)
     */
    private Quantity<Length> nodeDistance;

    /**
     * The area of a subsection.
     */
    private Quantity<Area> subsectionArea;

    /**
     * The volume of a subsection.
     */
    private Quantity<Volume> subsectionVolume;

    /**
     * The unit for concentrations in a subsection.
     */
    private Unit<MolarConcentration> subsectionConcentration;

    /**
     * The extend of the actual system.
     */
    private Quantity<Length> systemExtend;

    /**
     * Multiply the scale by a simulation distance to get the system distance.
     */
    private Quantity<Length> systemScale;

    /**
     * The extend of the simulation.
     */
    private double simulationExtend;

    /**
     * Multiply the scale by a system distance to get the simulation distance.
     */
    private double simulationScale;

    private static Environment getInstance() {
        if (instance == null) {
            synchronized (Environment.class) {
                instance = new Environment();
            }
        }
        return instance;
    }

    private Environment() {
        timeStep = DEFAULT_TIME_STEP;
        nodeDistance = DEFAULT_NODE_DISTANCE;
        subsectionArea = DEFAULT_NODE_DISTANCE.multiply(DEFAULT_NODE_DISTANCE).asType(Area.class);
        subsectionVolume = subsectionArea.multiply(DEFAULT_NODE_DISTANCE).asType(Volume.class);
        subsectionConcentration = NANO(MOLE).divide(subsectionVolume.getUnit()).asType(MolarConcentration.class);
        systemExtend = DEFAULT_SYSTEM_EXTEND;
        simulationExtend = DEFAULT_SIMULATION_EXTEND;
        systemTemperature = DEFAULT_TEMPERATURE;
        systemViscosity = DEFAULT_VISCOSITY;
        emptyConcentration = Quantities.getQuantity(0.0, subsectionConcentration);
        setSystemAnsSimulationScales();
        setChanged();
        notifyObservers();
    }

    public static void reset() {
        getInstance().timeStep = DEFAULT_TIME_STEP;
        getInstance().nodeDistance = DEFAULT_NODE_DISTANCE;
        getInstance().subsectionArea = DEFAULT_NODE_DISTANCE.multiply(DEFAULT_NODE_DISTANCE).asType(Area.class);
        getInstance().subsectionVolume = getSubsectionArea().multiply(DEFAULT_NODE_DISTANCE).asType(Volume.class);
        getInstance().subsectionConcentration = NANO(MOLE).divide(getSubsectionVolume().getUnit()).asType(MolarConcentration.class);
        getInstance().systemExtend = DEFAULT_SYSTEM_EXTEND;
        getInstance().simulationExtend = DEFAULT_SIMULATION_EXTEND;
        getInstance().systemTemperature = DEFAULT_TEMPERATURE;
        getInstance().systemViscosity = DEFAULT_VISCOSITY;
        getInstance().emptyConcentration = Quantities.getQuantity(0.0, getConcentrationUnit());
        getInstance().setSystemAnsSimulationScales();
        getInstance().setChanged();
        getInstance().notifyObservers();
    }

    public static Quantity<MolarConcentration> emptyConcentration() {
        return getInstance().emptyConcentration;
    }

    public static Quantity<Length> getNodeDistance() {
        return getInstance().nodeDistance;
    }

    public static Unit<Length> getNodeDistanceUnit() {
        return getInstance().nodeDistance.getUnit();
    }

    public static void setNodeDistance(Quantity<Length> nodeDistance) {
        logger.debug("Setting node distance to {}.", nodeDistance);
        getInstance().nodeDistance = nodeDistance;
        getInstance().adjustDistances();
        getInstance().setChanged();
        getInstance().notifyObservers();
    }

    private void adjustDistances() {
        // distance, area and volume
        Unit<Length> lengthUnit = nodeDistance.getUnit();
        Unit<Volume> subsectionVolumeUnit = lengthUnit.pow(3).asType(Volume.class);
        subsectionArea = nodeDistance.multiply(nodeDistance).asType(Area.class);
        subsectionVolume = subsectionArea.multiply(nodeDistance).asType(Volume.class);
        // concentration
        subsectionConcentration = NANO(MOLE).divide(subsectionVolumeUnit).asType(MolarConcentration.class);
        emptyConcentration = Quantities.getQuantity(0.0, subsectionConcentration);
    }

    public static Unit<MolarConcentration> getConcentrationUnit() {
        return getInstance().subsectionConcentration;
    }

    public static Quantity<Volume> getSubsectionVolume() {
        return getInstance().subsectionVolume;
    }

    public static Unit<Volume> getVolumeUnit() {
        return getInstance().subsectionVolume.getUnit();
    }

    public static Quantity<Area> getSubsectionArea() {
        return getInstance().subsectionArea;
    }

    public static Unit<Area> getAreaUnit() {
        return getInstance().subsectionArea.getUnit();
    }

    public static Quantity<Temperature> getTemperature() {
        return getInstance().systemTemperature;
    }

    public static void setTemperature(Quantity<Temperature> temperature) {
        logger.debug("Setting environmental temperature to {}.", temperature);
        getInstance().systemTemperature = temperature.to(KELVIN);
    }

    public static Quantity<DynamicViscosity> getViscosity() {
        return getInstance().systemViscosity;
    }

    public static void setSystemViscosity(Quantity<DynamicViscosity> viscosity) {
        logger.debug("Setting environmental dynamic viscosity of to {}.", viscosity);
        getInstance().systemViscosity = viscosity.to(MILLI(PASCAL_SECOND));
    }

    public static void setTimeStep(Quantity<Time> timeStep) {
        getInstance().timeStep = timeStep;
    }

    public static Quantity<Time> getTimeStep() {
        return getInstance().timeStep;
    }

    public static Unit<Time> getTimeUnit() {
        return getInstance().timeStep.getUnit();
    }

    public static void setNodeSpacingToDiameter(Quantity<Length> diameter, int spanningNodes) {
        logger.debug("Setting system diameter to {} using {} spanning nodes.", diameter, spanningNodes);
        setNodeDistance(diameter.divide(spanningNodes));
    }

    public static Quantity<Length> getSystemExtend() {
        return getInstance().systemExtend;
    }

    public static void setSystemExtend(Quantity<Length> systemExtend) {
        getInstance().systemExtend = systemExtend;
        getInstance().setSystemAnsSimulationScales();
    }

    public static double getSimulationExtend() {
        return getInstance().simulationExtend;
    }

    public static void setSimulationExtend(double simulationExtend) {
        getInstance().simulationExtend = simulationExtend;
        getInstance().setSystemAnsSimulationScales();
    }

    public static Quantity<Length> getSystemScale() {
        return getInstance().systemScale;
    }

    public static double getSimulationScale() {
        return getInstance().simulationScale;
    }

    private void setSystemAnsSimulationScales() {
        simulationScale = simulationExtend / systemExtend.getValue().doubleValue();
        systemScale = systemExtend.divide(simulationExtend);
    }

    public static Quantity<Length> convertSimulationToSystemScale(double simulationDistance) {
        return getInstance().systemScale.multiply(simulationDistance);
    }

    public static double convertSystemToSimulationScale(Quantity<Length> realDistance) {
        return realDistance.to(getInstance().systemExtend.getUnit()).getValue().doubleValue() * getInstance().simulationScale;
    }

    public static void attachObserver(Observer observer) {
        getInstance().addObserver(observer);
    }

    public static String report() {
        return "Environment: \n" +
                "time step = " + getInstance().timeStep + "\n" +
                "node distance = " + getInstance().nodeDistance + "\n" +
                "subsection area = " + getInstance().subsectionArea + "\n" +
                "subsection volume = " + getInstance().subsectionVolume + "\n" +
                "subsection concentration = " + getInstance().subsectionConcentration + "\n" +
                "system extend = " + getInstance().systemExtend + "\n" +
                "simulation extend = " + getInstance().simulationExtend + "\n" +
                "system temperature = " + getInstance().systemTemperature + "\n" +
                "system viscosity = " + getInstance().systemViscosity + "\n";
    }

}
