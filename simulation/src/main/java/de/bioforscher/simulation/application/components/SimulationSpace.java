package de.bioforscher.simulation.application.components;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.vectors.Vector2D;

public class SimulationSpace {

    public static final double DEFAULT_WIDTH = 400.0;
    public static final double DEFAULT_HEIGHT = 400.0;

    private Rectangle rectangle;

    private static SimulationSpace instance;

    private SimulationSpace() {
        this.rectangle = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
    }

    public static synchronized SimulationSpace getInstance() {
        if (instance == null) {
            instance = new SimulationSpace();
        }
        return instance;
    }

    public void reinitialize(double width, double height) {
        this.rectangle = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
        System.out.println(this.rectangle);
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public double getWidth() {
        return this.rectangle.getWidth();
    }

    public double getHeight() {
        return this.rectangle.getHeight();
    }

}
