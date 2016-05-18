package de.bioforscher.simulation.util;

import de.bioforscher.units.quantities.DynamicViscosity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import java.util.Observable;

import static de.bioforscher.units.UnitDictionary.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.KELVIN;

public class EnvironmentalVariables extends Observable {

    private static EnvironmentalVariables instance;

    public static EnvironmentalVariables getInstance() {
        if (instance == null) {
            synchronized (EnvironmentalVariables.class) {
                instance = new EnvironmentalVariables();
            }
        }
        return instance;
    }

    private Quantity<Length> nodeDistance;
    private Quantity<Time> timeStep;
    private Quantity<Temperature> systemTemperature;
    private Quantity<DynamicViscosity> systemViscosity;
    private boolean isCellularEnvironment;

    private EnvironmentalVariables() {
        resetToDefaultValues();
    }

    public void resetToDefaultValues() {
        this.nodeDistance = SystemDefaultConstants.NODE_DISTANCE;
        this.timeStep = SystemDefaultConstants.TIME_STEP;
        this.systemTemperature = SystemDefaultConstants.SYSTEM_TEMPERATURE;
        this.systemViscosity = SystemDefaultConstants.SYSTEM_VISCOSITY;
        this.isCellularEnvironment = false;
        setChanged();
        notifyObservers();
    }

    public Quantity<Length> getNodeDistance() {
        return nodeDistance;
    }

    public Quantity<Temperature> getSystemTemperature() {
        return systemTemperature;
    }

    public Quantity<DynamicViscosity> getSystemViscosity() {
        return systemViscosity;
    }

    public Quantity<Time> getTimeStep() {
        return timeStep;
    }

    public boolean isCellularEnvironment() {
        return isCellularEnvironment;
    }

    public void setCellularEnvironment(boolean isCellularEnvironment) {
        this.isCellularEnvironment = isCellularEnvironment;
        setChanged();
        notifyObservers();
    }

    public void setNodeDistance(Quantity<Length> nodeDistance) {
        this.nodeDistance = nodeDistance;
        setChanged();
        notifyObservers();
    }

    /**
     * @param diameter
     * @param spanningNodes
     */
    public void setNodeSpacingToDiameter(Quantity<Length> diameter, int spanningNodes) {
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
        this.timeStep = timeStep;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "EnvironmentalVariables [nodeDistance=" + nodeDistance
                + ", timeStep=" + timeStep + ", systemTemperature="
                + systemTemperature + ", systemViscosity=" + systemViscosity
                + "]";
    }


}
