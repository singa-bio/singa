package de.bioforscher.singa.simulation.application.components.plots;

import com.sun.javafx.geom.Point2D;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.application.components.panes.ModuleOverviewPane;
import de.bioforscher.singa.simulation.modules.diffusion.DiffusionUtilities;
import de.bioforscher.singa.simulation.model.parameters.EnvironmentalParameters;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class SimulationRobustnessPlot extends LineChart<Number, Number> {

    private XYChart.Data<Number, Number> indicator;
    private List<Vector2D> threshold;
    private ModuleOverviewPane owner;

    public SimulationRobustnessPlot(ModuleOverviewPane owner) {
        super(new NumberAxis(), new NumberAxis());
        this.owner = owner;
        configureChart();
        configureXAxis();
        configureYAxis();
        configureIndicator();
        configureThreshold();
    }

    @Override
    protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
        // suppress printing of points
    }

    private void configureChart() {
        this.setAnimated(false);
        this.setLegendVisible(false);
        this.setOnMouseClicked(this::handleCentering);
        this.setOnScroll(this::handleScroll);
    }

    private void handleScroll(ScrollEvent event) {
        double xAxisLowerBond = ((NumberAxis) this.getXAxis()).getLowerBound();
        double xAxisUpperBond = ((NumberAxis) this.getXAxis()).getUpperBound();
        double yAxisLowerBond = ((NumberAxis) this.getYAxis()).getLowerBound();
        double yAxisUpperBond = ((NumberAxis) this.getYAxis()).getUpperBound();

        if (event.getDeltaY() > 0) {
            ((NumberAxis) this.getXAxis()).setLowerBound(xAxisLowerBond <= 0 ? xAxisLowerBond : xAxisLowerBond - 1);
            ((NumberAxis) this.getXAxis()).setUpperBound(xAxisUpperBond + 1);
            ((NumberAxis) this.getYAxis()).setLowerBound(yAxisLowerBond <= 0 ? yAxisLowerBond : yAxisLowerBond - 1);
            ((NumberAxis) this.getYAxis()).setUpperBound(yAxisUpperBond + 1);
        } else {
            ((NumberAxis) this.getXAxis()).setLowerBound(xAxisLowerBond <= 0 ? xAxisLowerBond : xAxisLowerBond + 1);
            ((NumberAxis) this.getXAxis()).setUpperBound(xAxisUpperBond - 1);
            ((NumberAxis) this.getYAxis()).setLowerBound(yAxisLowerBond <= 0 ? yAxisLowerBond : yAxisLowerBond + 1);
            ((NumberAxis) this.getYAxis()).setUpperBound(yAxisUpperBond - 1);
        }
    }

    private void handleCentering(MouseEvent event) {
        if (event.getClickCount() >= 2) {
            centerOnIndicator();
        }
    }

    public void centerOnIndicator() {
        double xAxisLowerBond = ((NumberAxis) this.getXAxis()).getLowerBound();
        double xAxisUpperBond = ((NumberAxis) this.getXAxis()).getUpperBound();
        double yAxisLowerBond = ((NumberAxis) this.getYAxis()).getLowerBound();
        double yAxisUpperBond = ((NumberAxis) this.getYAxis()).getUpperBound();
        double xAxisRange = xAxisUpperBond - xAxisLowerBond;
        double yAxisRange = yAxisUpperBond - yAxisLowerBond;

        double x = this.indicator.getXValue().doubleValue();
        double y = this.indicator.getYValue().doubleValue();

        ((NumberAxis) this.getXAxis()).setLowerBound(x - xAxisRange / 2);
        ((NumberAxis) this.getXAxis()).setUpperBound(x + xAxisRange / 2);
        ((NumberAxis) this.getYAxis()).setLowerBound(y - yAxisRange / 2);
        ((NumberAxis) this.getYAxis()).setUpperBound(y + yAxisRange / 2);
    }

    private void configureXAxis() {
        this.getXAxis().labelProperty().bind(this.owner.getEnvironmentalControl().getNodeDistanceUnitProperty()
                                                       .asString());
        this.getXAxis().setAutoRanging(false);
    }

    private void configureYAxis() {
        this.getYAxis().labelProperty().bind(this.owner.getEnvironmentalControl().getTimeStepSizeUnitProperty()
                                                       .asString());
        this.getYAxis().setAutoRanging(false);
    }

    private void configureIndicator() {
        XYChart.Series<Number, Number> indicatorSeries = new XYChart.Series<>();
        this.indicator = new XYChart.Data<>();
        indicatorSeries.getData().add(this.indicator);
        this.getData().add(indicatorSeries);
        // configure dragging
        Node node = this.indicator.getNode();
        node.setCursor(Cursor.HAND);
        node.setOnMouseDragged(this::handleMouseDragged);
    }

    private void configureThreshold() {

        Series<Number, Number> thresholdSeries = new Series<>();
        thresholdSeries.setName("threshold");
        this.getData().add(thresholdSeries);

        boolean isInRange = true;
        int timeStepCounter = 0;

        while (isInRange) {
            // calculate value
            Quantity<Time> timeStep = Quantities.getQuantity(timeStepCounter, EnvironmentalParameters
                    .getInstance().getTimeStep().getUnit());
            Quantity<Length> distance = DiffusionUtilities.calculateThresholdForDistance(timeStep, this.owner
                    .getMaximalDegree(), this.owner.getMaximalDifference(), this.owner.getMaximalDiffusivity()
                                                                                      .multiply(10000));
            // check if still in range
            if (distance.getValue().doubleValue() < 1000) {
                // add data
                thresholdSeries.getData().add(new XYChart.Data<>(distance.getValue(), timeStepCounter));
                // add time
                timeStepCounter += +1;
            } else {
                isInRange = false;
            }

            if (timeStepCounter >= 1000) {
                isInRange = false;
            }

        }
        this.threshold = convertThreshold(thresholdSeries);
    }

    public Data<Number, Number> getIndicator() {
        return this.indicator;
    }

    private List<Vector2D> convertThreshold(Series<Number, Number> thresholdSeries) {
        return thresholdSeries.getData().stream()
                            .map(data -> new Vector2D(data.getXValue().doubleValue(), data.getYValue().doubleValue()))
                            .collect(Collectors.toList());
    }

    public List<Vector2D> getThreshold() {
        return this.threshold;
    }

    private void handleMouseDragged(MouseEvent event) {
        Point2D pointInScene = new Point2D((float) event.getSceneX(), (float) event.getSceneY());
        double xAxisLoc = this.getXAxis().sceneToLocal(pointInScene.x, pointInScene.y).getX();
        double yAxisLoc = this.getYAxis().sceneToLocal(pointInScene.x, pointInScene.y).getY();
        Number x = this.getXAxis().getValueForDisplay(xAxisLoc);
        Number y = this.getYAxis().getValueForDisplay(yAxisLoc);

        if (event.isPrimaryButtonDown()) {
            if (x.doubleValue() > 0.1 && y.doubleValue() > 0.1 && x.doubleValue() < 1000 && y.doubleValue() < 1000) {
                this.indicator.setXValue(x);
                this.indicator.setYValue(y);
            }
        }


    }

}
