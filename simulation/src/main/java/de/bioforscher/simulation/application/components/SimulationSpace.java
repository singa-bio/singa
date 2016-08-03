package de.bioforscher.simulation.application.components;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SimulationSpace {

    public static final double DEFAULT_WIDTH = 400.0;
    public static final double DEFAULT_HEIGHT = 400.0;

    private DoubleProperty width;
    private DoubleProperty height;

    private static SimulationSpace instance;

    private SimulationSpace() {
        this.width = new SimpleDoubleProperty(DEFAULT_WIDTH);
        this.height = new SimpleDoubleProperty(DEFAULT_WIDTH);
    }

    public static synchronized SimulationSpace getInstance() {
        if (instance == null) {
            instance = new SimulationSpace();
        }
        return instance;
    }

    public Rectangle getRectangle() {
        return new Rectangle(width.getValue(), height.getValue());
    }

    public DoubleProperty getWidth() {
        return this.width;
    }

    public DoubleProperty getHeight() {
        return this.height;
    }

}
