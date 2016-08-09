package de.bioforscher.simulation.application.components;

import com.sun.javafx.geom.Point2D;
import de.bioforscher.units.quantities.Diffusivity;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;

import javax.measure.Quantity;

/**
 * Created by Christoph on 05.08.2016.
 */
public class SimulationRobustnessPlot extends LineChart<Number, Number> {

    private XYChart.Data<Number, Number> indicator;

    private Quantity<Diffusivity> diffusivity;
    private int maximalDegree;

    public SimulationRobustnessPlot(Quantity<Diffusivity> diffusivity, int maximalDegree) {
        super(new NumberAxis(), new NumberAxis());
        this.diffusivity = diffusivity;
        this.maximalDegree = maximalDegree;
        configureIndicator();
        configureThreshold();
        configureChart();
    }

    private void configureChart() {
        this.setAnimated(false);
    }

    private void configureIndicator() {
        XYChart.Series<Number, Number> indicatorSeries = new XYChart.Series<>();
        this.indicator = new XYChart.Data<>();
        indicatorSeries.getData().add(this.indicator);
        this.getData().add(indicatorSeries);
        // configure dragging
        Node node = this.indicator.getNode() ;
        node.setCursor(Cursor.HAND);
        node.setOnMouseDragged(this::handleMouseDragged);
    }

    private void configureThreshold() {
        XYChart.Series<Number, Number> thresholdSeries = new XYChart.Series<>();
        for(int timeStep = 10; timeStep < 500; timeStep++) {
            // TODO this needs way mor research
            double minimalDistance = Math.sqrt((this.maximalDegree * this.diffusivity.multiply(10000).getValue()
                                                                                     .doubleValue()-1)*timeStep);
            thresholdSeries.getData().add(new XYChart.Data<>(minimalDistance, timeStep));
        }
        this.getData().add(thresholdSeries);
    }

    public Data<Number, Number> getIndicator() {
        return this.indicator;
    }

    private void handleMouseDragged(MouseEvent event) {
        Point2D pointInScene = new Point2D((float)event.getSceneX(), (float)event.getSceneY());
        double xAxisLoc = this.getXAxis().sceneToLocal(pointInScene.x, pointInScene.y).getX();
        double yAxisLoc = this.getYAxis().sceneToLocal(pointInScene.x, pointInScene.y).getY();
        Number x = this.getXAxis().getValueForDisplay(xAxisLoc);
        Number y = this.getYAxis().getValueForDisplay(yAxisLoc);
        if (x.doubleValue() > 0.1 && y.doubleValue() > 0.1) {
            this.indicator.setXValue(x);
            this.indicator.setYValue(y);
        }
    }

}
