package de.bioforscher.simulation.application.components.modules;

import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.EnvironmentalOptionsControlPanel;
import de.bioforscher.simulation.application.components.SimulationRobustnessPlot;
import de.bioforscher.simulation.modules.AvailableModule;
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
import javafx.util.converter.NumberStringConverter;

import javax.measure.Quantity;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Christoph on 03.08.2016.
 */
public class ModuleOverviewPane extends SplitPane {

    private BioGraphSimulation owner;
    private ListView<AvailableModule> moduleList = new ListView<>();
    private GridPane detailGrid = new GridPane();
    private GridPane descriptionGrid = new GridPane();
    private EnvironmentalOptionsControlPanel environmentalControl = new EnvironmentalOptionsControlPanel();
    private GridPane indicatorsGrid = new GridPane();
    private SimulationRobustnessPlot chartPlaceHolder;

    private Label robustnessLabel = new Label("Robustness:");
    private Label accuracyLabel = new Label("Accuracy:");
    private Label runtimeLabel = new Label("Runtime:");
    private Label robustnessValueLabel = new Label("0.0");
    private Label accuracyValueLabel = new Label("0.0");
    private Label runtimeValueLabel = new Label("0.0");
    private Slider robustnessSlider = new Slider(-1.0, 1.0, 0.0);
    private Slider accuracySlider = new Slider(-1.0, 1.0, 0.0);
    private Slider runtimeSlider = new Slider(-1.0, 1.0, 0.0);

    private Quantity<Diffusivity> maximalDiffusivity;
    private Quantity<MolarConcentration> maximalDifference;
    private int maximalDegree;

    private ObservableList<AvailableModule> modules = FXCollections.observableArrayList();

    public ModuleOverviewPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.modules.addAll(AvailableModule.values());
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


    public void colorAfterShow() {
        String gradientTrackStyle = "-fx-background-color:\n" +
                "derive(-fx-color,-36%),\n" +
                "derive(-fx-color,73%),\n" +
                "linear-gradient(to right, #d7191c, #fdae61, #ffffbf, #a6d96a, #1a9641);";
        this.accuracySlider.getChildrenUnmodifiable().get(1).setStyle(gradientTrackStyle);
        this.robustnessSlider.getChildrenUnmodifiable().get(1).setStyle(gradientTrackStyle);
        this.runtimeSlider.getChildrenUnmodifiable().get(1).setStyle(gradientTrackStyle);
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
    }

    private void configureDetailGrid() {
        this.detailGrid.setHgap(10);
        this.detailGrid.setVgap(10);
        this.detailGrid.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints column0 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
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
        this.robustnessSlider.setMajorTickUnit(1);
        this.robustnessSlider.setShowTickMarks(false);
        this.robustnessSlider.setShowTickLabels(true);

        ObjectProperty<Number> distanceProperty = this.environmentalControl.getNodeDistanceProperty();
        ObjectProperty<Number> timeStepProperty = this.environmentalControl.getTimeStepSizeProperty();

        distanceProperty.addListener(change -> {

            double distance = distanceProperty.getValue().doubleValue();
            double timestep = timeStepProperty.getValue().doubleValue();
            double maxDeg = this.maximalDegree;
            double maxCon = this.maximalDifference.getValue().doubleValue();
            double maxDif = this.maximalDiffusivity.getValue().doubleValue()*10000;

            this.robustnessSlider.valueProperty().setValue(Math.sqrt(maxDeg*maxDif-maxCon)*timestep);
            System.out.println(Math.sqrt(maxDeg*maxDif-maxCon)*timestep-distance*distance);
        });



        /*this.robustnessSlider.setLabelFormatter(new StringConverter<Double>() {

            @Override
            public String toString(Double n) {
                if (n == -1.0) return "unstable";
                if (n == 1.0) return "robust";
                return "";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "unstable":
                        return -1d;
                    case "robust":
                        return 1d;

                    default:
                        return 0d;
                }
            }

        });*/

        this.accuracySlider.setMinorTickCount(0);
        this.accuracySlider.setMajorTickUnit(1);
        this.accuracySlider.setShowTickMarks(false);
        this.accuracySlider.setShowTickLabels(true);
        this.accuracySlider.setLabelFormatter(new StringConverter<Double>() {

            @Override
            public String toString(Double n) {
                if (n == -1.0) return "diverging";
                if (n == 1.0) return "precise";
                return "";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "diverging":
                        return -1d;
                    case "precise":
                        return 1d;

                    default:
                        return 0d;
                }
            }

        });

        this.runtimeSlider.setMinorTickCount(0);
        this.runtimeSlider.setMajorTickUnit(1);
        this.runtimeSlider.setShowTickMarks(false);
        this.runtimeSlider.setShowTickLabels(true);
        this.runtimeSlider.setLabelFormatter(new StringConverter<Double>() {

            @Override
            public String toString(Double n) {
                if (n == -1.0) return "slow";
                if (n == 1.0) return "fast";
                return "";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "slow":
                        return -1d;
                    case "fast":
                        return 1d;

                    default:
                        return 0d;
                }
            }

        });

    }

    private void configureSliderLabels() {
        StringConverter<Number> converter = new NumberStringConverter();
        this.robustnessValueLabel.textProperty().bindBidirectional(this.robustnessSlider.valueProperty(), converter);
        this.accuracyValueLabel.textProperty().bindBidirectional(this.accuracySlider.valueProperty(), converter);
        this.runtimeValueLabel.textProperty().bindBidirectional(this.runtimeSlider.valueProperty(), converter);
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
        this.chartPlaceHolder = new SimulationRobustnessPlot(this.maximalDiffusivity, this.maximalDegree);
        ObjectProperty<Number> indicatorXPositionProperty = this.chartPlaceHolder.getIndicator().XValueProperty();
        ObjectProperty<Number> nodeDistanceProperty = this.environmentalControl.getNodeDistanceProperty();
        indicatorXPositionProperty.bindBidirectional(nodeDistanceProperty);
        ObjectProperty<Number> indicatorYPositionProperty = this.chartPlaceHolder.getIndicator().YValueProperty();
        ObjectProperty<Number> timeStepProperty = this.environmentalControl.getTimeStepSizeProperty();
        indicatorYPositionProperty.bindBidirectional(timeStepProperty);
    }

    private void addComponentsToDetailGrid() {
        this.detailGrid.add(this.descriptionGrid, 0, 0, 2, 1);
        this.detailGrid.add(this.environmentalControl, 0, 1, 1, 2);
        this.detailGrid.add(this.indicatorsGrid, 1, 1);
        this.detailGrid.add(this.chartPlaceHolder, 1, 2);
    }

    private void addComponentsToSplit() {
        this.getItems().add(this.moduleList);
        this.getItems().add(this.detailGrid);
    }


}
