package de.bioforscher.simulation.application.components.panes;

import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.cells.ModuleCell;
import de.bioforscher.simulation.application.components.controlpanles.EnvironmentalParameterControlPanel;
import de.bioforscher.simulation.application.components.plots.SimulationRobustnessPlot;
import de.bioforscher.simulation.modules.model.AvailableModules;
import de.bioforscher.simulation.modules.diffusion.DiffusionUtilities;
import de.bioforscher.units.quantities.Diffusivity;
import de.bioforscher.units.quantities.MolarConcentration;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import javax.measure.Quantity;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class ModuleOverviewPane extends SplitPane {

    private BioGraphSimulation owner;
    private ListView<AvailableModules> moduleList = new ListView<>();
    private GridPane detailGrid = new GridPane();
    private GridPane descriptionGrid = new GridPane();
    private EnvironmentalParameterControlPanel environmentalControl = new EnvironmentalParameterControlPanel();
    private GridPane indicatorsGrid = new GridPane();
    private SimulationRobustnessPlot simulationRobustnessPlot;

    private Label robustnessLabel = new Label("Robustness:");
    private Label accuracyLabel = new Label("Accuracy:");
    private Label runtimeLabel = new Label("Runtime:");
    private Label robustnessValueLabel = new Label("0.0");
    private Label accuracyValueLabel = new Label("0.0");
    private Label runtimeValueLabel = new Label("0.0");
    private Slider robustnessSlider = new Slider(-10.0, 10.0, 0.0);
    private Slider accuracySlider = new Slider(7.0, 17.0, 0.0);
    private Slider runtimeSlider = new Slider(0.0, 100000.0, 0.0);

    private Quantity<Diffusivity> maximalDiffusivity;
    private Quantity<MolarConcentration> maximalDifference;
    private int numberOfNodes;
    private int maximalDegree;

    private ObservableList<AvailableModules> modules = FXCollections.observableArrayList();

    public ModuleOverviewPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.modules.addAll(AvailableModules.values());
        configureSplit();
        configureModuleList();
        configureDetailGrid();
        configureEnvironmentalControl();
        configureDescriptionGrid();
        configureIndicatorsGrid();
        configureSliderLabels();
        configureSliders();
        addComponentsToIndicatorGrid();
        configureChart();
        addComponentsToDetailGrid();
        addComponentsToSplit();
    }

    public void doAfterShow() {
        String redGreenGradientTrack = "-fx-background-color:\n" +
                "derive(-fx-color,-36%),\n" +
                "derive(-fx-color,73%),\n" +
                "linear-gradient(to right, #d7191c, #fdae61, #ffffbf, #a6d96a, #1a9641);";
        String greenRedGradientTrack = "-fx-background-color:\n" +
                "derive(-fx-color,-36%),\n" +
                "derive(-fx-color,73%),\n" +
                "linear-gradient(to right, #1a9641, #a6d96a, #ffffbf, #fdae61, #d7191c);";

        this.robustnessSlider.getChildrenUnmodifiable().get(1).setStyle(redGreenGradientTrack);
        this.accuracySlider.getChildrenUnmodifiable().get(1).setStyle(greenRedGradientTrack);
        this.runtimeSlider.getChildrenUnmodifiable().get(1).setStyle(greenRedGradientTrack);
    }

    private void configureSplit() {
        this.setDividerPosition(0, 0.22);

    }

    private void configureModuleList() {
        this.moduleList.setCellFactory(module -> new ModuleCell());
        this.moduleList.setItems(this.modules);
        this.moduleList.maxWidthProperty().bind(this.widthProperty().multiply(0.22));
    }

    private void configureEnvironmentalControl() {
        NumberFormat formatter = new DecimalFormat("0.000E0");
        this.maximalDegree = this.owner.getGraph().getMaximumDegree();
        this.maximalDiffusivity = this.owner.getSimulation().getFreeDiffusionModule().getMaximalDiffusivity();
        this.maximalDifference = this.owner.getGraph().getSteepestDifference(
                this.owner.getSimulation().getFreeDiffusionModule().getEntityWithMaximalDiffusivity());
        this.environmentalControl.getMaximalDegreeProperty()
                                 .setValue(String.valueOf(this.maximalDegree));
        this.environmentalControl.getMaximalDiffusivityProperty().setValue(formatter.format(this.maximalDiffusivity
                .getValue()));
        this.environmentalControl.getMaximalConcentrationDiffenceProperty().setValue(this.maximalDifference.getValue()
                                                                                                           .toString());
        this.numberOfNodes = this.owner.getGraph().getNodes().size();
    }

    private void configureDetailGrid() {
        this.detailGrid.setHgap(10);
        this.detailGrid.setVgap(10);
        this.detailGrid.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints column0 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT,
                true);
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT,
                true);
        this.detailGrid.getColumnConstraints().add(0, column0);
        this.detailGrid.getColumnConstraints().add(1, column1);

        RowConstraints row0 = new RowConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, VPos.TOP, true);
        this.detailGrid.getRowConstraints().add(0, row0);
    }

    private void configureDescriptionGrid() {
        this.descriptionGrid.setHgap(10);
        this.descriptionGrid.setVgap(10);
        this.descriptionGrid.setPadding(new Insets(10, 10, 10, 10));
        this.descriptionGrid.setBackground(new Background(new BackgroundFill(Color.DARKSEAGREEN, null, null)));
    }

    private void configureIndicatorsGrid() {
        this.indicatorsGrid.setHgap(10);
        this.indicatorsGrid.setVgap(10);
        this.indicatorsGrid.setPadding(new Insets(10, 10, 10, 10));
    }

    private void configureSliders() {
        this.robustnessSlider.setMinorTickCount(0);
        this.robustnessSlider.setMajorTickUnit(10);
        this.robustnessSlider.setPrefWidth(200);
        this.robustnessSlider.setDisable(true);
        this.robustnessSlider.setStyle("-fx-opacity: 1");
        this.robustnessSlider.setShowTickMarks(false);
        this.robustnessSlider.setShowTickLabels(true);

        this.accuracySlider.setMinorTickCount(0);
        this.accuracySlider.setMajorTickUnit(2);
        this.accuracySlider.setDisable(true);
        this.accuracySlider.setStyle("-fx-opacity: 1");
        this.accuracySlider.setShowTickMarks(false);
        this.accuracySlider.setShowTickLabels(true);

        this.runtimeSlider.setMinorTickCount(0);
        this.runtimeSlider.setMajorTickUnit(50000);
        this.runtimeSlider.setDisable(true);
        this.runtimeSlider.setStyle("-fx-opacity: 1");
        this.runtimeSlider.setShowTickMarks(false);
        this.runtimeSlider.setShowTickLabels(true);

        ObjectProperty<Number> distanceProperty = this.environmentalControl.getNodeDistanceProperty();
        ObjectProperty<Number> timeStepProperty = this.environmentalControl.getTimeStepSizeProperty();

        timeStepProperty.addListener(change -> {
            double runtimeEstimate = DiffusionUtilities.estimateSimulationSpeed(timeStepProperty.getValue().doubleValue(),
                    this.numberOfNodes);
            this.runtimeSlider.valueProperty().setValue(runtimeEstimate);
            double accuracyEstimate = DiffusionUtilities.estimateSimulationAccuracy(timeStepProperty.getValue().doubleValue(),
                    distanceProperty.getValue().doubleValue());
            this.accuracySlider.valueProperty().setValue(accuracyEstimate);
        });

        distanceProperty.addListener(change -> {
            Vector2D indicator = new Vector2D(distanceProperty.getValue().doubleValue(), timeStepProperty.getValue().doubleValue());
            List<Vector2D> list = this.simulationRobustnessPlot.getThreshold();

            Map.Entry<Vector2D, Double> closest = VectorMetricProvider.MANHATTAN_METRIC.calculateClosestDistance(list, indicator);

            if (DiffusionUtilities.areViableParametersForDiffusion(timeStepProperty.getValue().doubleValue(),
                    distanceProperty.getValue().doubleValue(), this.maximalDegree, this.maximalDifference, this
                            .maximalDiffusivity.multiply(10000))) {
                this.robustnessSlider.valueProperty().setValue(closest.getValue());
            } else {
                this.robustnessSlider.valueProperty().setValue(-closest.getValue());
            }

        });

    }

    private void configureSliderLabels() {
        StringConverter<Number> robustnessConverter = new StringConverter<Number>() {

            @Override
            public String toString(Number n) {
                if (n.doubleValue() < -3.0) return "unstable";
                if (n.doubleValue() < 1.0) return "probably unstable";
                if (n.doubleValue() < 4.0) return "probably robust";
                return "robust";
            }

            @Override
            public Number fromString(String s) {
                switch (s) {
                    case "unstable":
                        return -2;
                    case "probably unstable":
                        return 0;
                    case "probably robust":
                        return 3;
                    default:
                        return 4;
                }
            }

        };
        StringConverter<Number> runtimeConverter = new StringConverter<Number>() {

            @Override
            public String toString(Number n) {
                if (n.intValue() < 10000) return "very fast";
                if (n.intValue() < 40000) return "fast";
                if (n.intValue() < 80000) return "slow";
                return "very slow";
            }

            @Override
            public Number fromString(String s) {
                switch (s) {
                    case "very fast":
                        return 9999;
                    case "fast":
                        return 39999;
                    case "slow":
                        return 79999;
                    default:
                        return 80000;
                }
            }

        };
        StringConverter<Number> accuracyConverter =new StringConverter<Number>() {

            @Override
            public String toString(Number n) {
                if (n.doubleValue() <= 9 ) return "very precise";
                if (n.doubleValue() <= 12.0) return "precise";
                if (n.doubleValue() <= 14.0) return "diverging";
                return "very diverging";
            }

            @Override
            public Number fromString(String s) {
                switch (s) {
                    case "very precise":
                        return 8;
                    case "precise":
                        return 11;
                    case "diverging":
                        return 13;
                    default:
                        return 14;
                }
            }

        };
        this.robustnessValueLabel.textProperty().bindBidirectional(this.robustnessSlider.valueProperty(), robustnessConverter);
        this.accuracyValueLabel.textProperty().bindBidirectional(this.accuracySlider.valueProperty(), accuracyConverter);
        this.runtimeValueLabel.textProperty().bindBidirectional(this.runtimeSlider.valueProperty(), runtimeConverter);
    }

    private void addComponentsToIndicatorGrid() {
        this.indicatorsGrid.add(this.robustnessLabel, 0, 0);
        this.indicatorsGrid.add(this.robustnessValueLabel, 1, 0);
        this.indicatorsGrid.add(this.robustnessSlider, 0, 1, 2, 1);
        this.indicatorsGrid.add(this.accuracyLabel, 0, 2);
        this.indicatorsGrid.add(this.accuracyValueLabel, 1, 2);
        this.indicatorsGrid.add(this.accuracySlider, 0, 3, 2, 1);
        this.indicatorsGrid.add(this.runtimeLabel, 0, 4);
        this.indicatorsGrid.add(this.runtimeValueLabel, 1, 4);
        this.indicatorsGrid.add(this.runtimeSlider, 0, 5, 2, 1);
    }

    private void configureChart() {
        this.simulationRobustnessPlot = new SimulationRobustnessPlot(this);
        ObjectProperty<Number> indicatorXPositionProperty = this.simulationRobustnessPlot.getIndicator().XValueProperty();
        ObjectProperty<Number> nodeDistanceProperty = this.environmentalControl.getNodeDistanceProperty();
        indicatorXPositionProperty.bindBidirectional(nodeDistanceProperty);
        ObjectProperty<Number> indicatorYPositionProperty = this.simulationRobustnessPlot.getIndicator().YValueProperty();
        ObjectProperty<Number> timeStepProperty = this.environmentalControl.getTimeStepSizeProperty();
        indicatorYPositionProperty.bindBidirectional(timeStepProperty);
    }

    private void addComponentsToDetailGrid() {
        // this.detailGrid.add(this.descriptionGrid, 0, 0, 2, 1);
        this.detailGrid.add(this.simulationRobustnessPlot, 0, 0, 2, 1);
        this.detailGrid.add(this.environmentalControl, 0, 2);
        this.detailGrid.add(this.indicatorsGrid, 1, 2);

    }

    private void addComponentsToSplit() {
        this.getItems().add(this.moduleList);
        this.getItems().add(this.detailGrid);
    }

    public EnvironmentalParameterControlPanel getEnvironmentalControl() {
        return this.environmentalControl;
    }

    public Quantity<Diffusivity> getMaximalDiffusivity() {
        return this.maximalDiffusivity;
    }

    public Quantity<MolarConcentration> getMaximalDifference() {
        return this.maximalDifference;
    }

    public int getMaximalDegree() {
        return this.maximalDegree;
    }

}
