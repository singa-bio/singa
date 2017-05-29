package de.bioforscher.singa.units.parameters;

import de.bioforscher.singa.units.quantities.DynamicViscosity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import java.util.Observable;

import static de.bioforscher.singa.units.UnitProvider.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.KELVIN;

public class EnvironmentalParameters extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentalParameters.class);

    private static EnvironmentalParameters instance;

    private Quantity<Length> nodeDistance;
    private Quantity<Time> timeStep;
    private Quantity<Temperature> systemTemperature;
    private Quantity<DynamicViscosity> systemViscosity;
    private boolean isCellularEnvironment;

    public static EnvironmentalParameters getInstance() {
        if (instance == null) {
            synchronized (EnvironmentalParameters.class) {
                instance = new EnvironmentalParameters();
            }
        }
        return instance;
    }

    private EnvironmentalParameters() {
        resetToDefaultValues();
    }

    public void resetToDefaultValues() {
        this.nodeDistance = EnvironmentalParameterDefaults.NODE_DISTANCE;
        this.timeStep = EnvironmentalParameterDefaults.TIME_STEP;
        this.systemTemperature = EnvironmentalParameterDefaults.SYSTEM_TEMPERATURE;
        this.systemViscosity = EnvironmentalParameterDefaults.SYSTEM_VISCOSITY;
        this.isCellularEnvironment = false;
        setChanged();
        notifyObservers();
    }

    public Quantity<Length> getNodeDistance() {
        return this.nodeDistance;
    }

    public Quantity<Temperature> getSystemTemperature() {
        return this.systemTemperature;
    }

    public Quantity<DynamicViscosity> getSystemViscosity() {
        return this.systemViscosity;
    }

    public Quantity<Time> getTimeStep() {
        return this.timeStep;
    }

    public boolean isCellularEnvironment() {
        return this.isCellularEnvironment;
    }

    public void setCellularEnvironment(boolean isCellularEnvironment) {
        this.isCellularEnvironment = isCellularEnvironment;
        setChanged();
        notifyObservers();
    }

    public void setNodeDistance(Quantity<Length> nodeDistance) {
        logger.debug("Setting new spatial step size to {}.", nodeDistance);
        this.nodeDistance = nodeDistance;
        setChanged();
        notifyObservers();
    }

    public void setNodeSpacingToDiameter(Quantity<Length> diameter, int spanningNodes) {
        logger.debug("Setting system diameter to {} using {} spanning nodes.", diameter, spanningNodes);
        this.setNodeDistance(
                Quantities.getQuantity(diameter.getValue().doubleValue() / (spanningNodes - 1), diameter.getUnit()));
    }

    public void setSystemTemperature(Quantity<Temperature> systemTemperature) {
        // always in kelvin
        this.systemTemperature = systemTemperature.to(KELVIN);
        setChanged();
        notifyObservers();
    }

    public void setSystemViscosity(Quantity<DynamicViscosity> systemViscosity) {
        this.systemViscosity = systemViscosity.to(MILLI(PASCAL_SECOND));
        setChanged();
        notifyObservers();
    }

    public void setTimeStep(Quantity<Time> timeStep) {
        logger.debug("Setting new time step size to {}.", timeStep);
        this.timeStep = timeStep;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "EnvironmentalParameters [nodeDistance=" + this.nodeDistance
                + ", timeStep=" + this.timeStep + ", systemTemperature="
                + this.systemTemperature + ", systemViscosity=" + this.systemViscosity
                + "]";
    }


}
