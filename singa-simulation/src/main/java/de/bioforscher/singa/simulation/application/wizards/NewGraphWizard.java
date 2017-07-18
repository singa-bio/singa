package de.bioforscher.singa.simulation.application.wizards;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.DynamicViscosity;
import de.bioforscher.singa.features.units.UnitName;
import de.bioforscher.singa.features.units.UnitPrefix;
import de.bioforscher.singa.features.units.UnitPrefixes;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.features.units.UnitProvider.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.*;

/**
 * A wizard used to create a new graph.
 *
 * @author cl
 */
public class NewGraphWizard extends Wizard {

    private Stage owner;
    private AutomatonGraph graph = null;

    public NewGraphWizard(Stage owner) {
        super(new GraphConfigurationPage(), new EnvironmentalConfigurationPage());
        this.owner = owner;
    }

    @Override
    public void finish() {
        this.owner.close();
        if (this.getCurrentPage().getClass().equals(GraphConfigurationPage.class)) {
            setGraph(((GraphConfigurationPage) this.getCurrentPage()).createGraph());
        }
        if (this.getCurrentPage().getClass().equals(EnvironmentalConfigurationPage.class)) {
            ((EnvironmentalConfigurationPage) this.getCurrentPage()).createEnvironmentalVariables();
        }
    }

    @Override
    public void cancel() {
        this.graph = null;
        this.owner.close();
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

}

class GraphConfigurationPage extends WizardPage {

    private RadioButton rbRectangularGraph;
    private RadioButton rbRandomizedGraph;
    private Spinner<Integer> spNumberHorizontalNodes;
    private Spinner<Integer> spNumberVerticalNodes;
    private Spinner<Integer> spNumberNodes;
    private Spinner<Double> spConnectivity;
    private ToggleGroup tgMethods;

    GraphConfigurationPage() {
        super("Configure Graph");
        setDescription(
                "A new graph will be created. A regular rectangular graph or a randomized graph with a given number " +
                        "of nodes and a degree of connectivity can be automatically created. Use [Up] and [Down] " +
                        "Arrows to adjust values.");
        this.tgMethods = new ToggleGroup();
        this.rbRectangularGraph.setToggleGroup(this.tgMethods);
        this.rbRandomizedGraph.setToggleGroup(this.tgMethods);

        this.nextButton.setDisable(true);
        this.finishButton.setDisable(true);
        this.spNumberHorizontalNodes.setDisable(true);
        this.spNumberVerticalNodes.setDisable(true);
        this.spNumberNodes.setDisable(true);
        this.spConnectivity.setDisable(true);

        this.tgMethods.selectedToggleProperty().addListener(this::hideUnselected);

    }

    private void hideUnselected(ObservableValue<? extends Toggle> observableToggle, Toggle oldToggle, Toggle
            newToggle) {
        this.nextButton.setDisable(false);
        this.finishButton.setDisable(false);
        if (newToggle.getUserData().equals("RECTANGLE")) {
            this.spNumberHorizontalNodes.setDisable(false);
            this.spNumberVerticalNodes.setDisable(false);
            this.spNumberNodes.setDisable(true);
            this.spConnectivity.setDisable(true);
        } else if (newToggle.getUserData().equals("RANDOMIZED")) {
            this.spNumberHorizontalNodes.setDisable(true);
            this.spNumberVerticalNodes.setDisable(true);
            this.spNumberNodes.setDisable(false);
            this.spConnectivity.setDisable(false);
        }
    }

    @Override
    public Parent getContent() {

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(0, 10, 0, 10));

        // rectangular graph
        this.rbRectangularGraph = new RadioButton("Create rectangular Graph.");
        this.rbRectangularGraph.setUserData("RECTANGLE");
        content.add(this.rbRectangularGraph, 0, 0, 4, 1);

        Label labHorizontalNodes = new Label("Number of nodes horizontally:");
        content.add(labHorizontalNodes, 0, 1, 1, 1);

        this.spNumberHorizontalNodes = new Spinner<>(1, 100, 10);
        this.spNumberHorizontalNodes.setEditable(true);
        content.add(this.spNumberHorizontalNodes, 1, 1, 1, 1);

        Label labVerticalNodes = new Label("Number of Nodes vertically:");
        content.add(labVerticalNodes, 2, 1, 1, 1);

        this.spNumberVerticalNodes = new Spinner<>(1, 100, 10);
        this.spNumberVerticalNodes.setEditable(true);
        content.add(this.spNumberVerticalNodes, 3, 1, 1, 1);

        // randomized graph
        this.rbRandomizedGraph = new RadioButton("Create randomized graph (with Erdos-Renyi model).");
        this.rbRandomizedGraph.setUserData("RANDOMIZED");
        content.add(this.rbRandomizedGraph, 0, 2, 4, 1);

        Label labNumberNodes = new Label("Total number of nodes: ");
        content.add(labNumberNodes, 0, 3, 1, 1);

        this.spNumberNodes = new Spinner<>(1, 10000, 50);
        this.spNumberNodes.setEditable(true);
        content.add(this.spNumberNodes, 1, 3, 1, 1);

        Label labConnectivity = new Label("Connectivity in %: ");
        content.add(labConnectivity, 2, 3, 1, 1);

        this.spConnectivity = new Spinner<>(0, 1, 0.1, 0.01);
        this.spConnectivity.setEditable(true);
        content.add(this.spConnectivity, 3, 3, 1, 1);

        return new VBox(content);
    }

    public AutomatonGraph createGraph() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbRectangularGraph)) {
            return AutomatonGraphs.copyStructureToBioGraph(Graphs.buildGridGraph(
                    this.spNumberVerticalNodes.getValue(), this.spNumberHorizontalNodes.getValue(),
                    new Rectangle(400, 400), false));
        } else {
            return AutomatonGraphs
                    .copyStructureToBioGraph(Graphs.buildRandomGraph(this.spNumberNodes.getValue(),
                            this.spConnectivity.getValue(), new Rectangle(400, 400)));
        }
    }

    @Override
    public void navigateToNextPage() {
        ((NewGraphWizard) getWizard()).setGraph(createGraph());
        super.navigateToNextPage();
    }
}

class EnvironmentalConfigurationPage extends WizardPage {

    private Spinner<Integer> spNodeDistance;
    private Spinner<Integer> spTimeStep;
    private Spinner<Double> spTemperature;
    private Spinner<Double> spViscosity;

    private ComboBox<Unit<Length>> cbNodeDistance;
    private ComboBox<Unit<Time>> cbTimeStep;

    public EnvironmentalConfigurationPage() {
        super("Environmental Options");
        setDescription(
                "A new graph will be created. A regular rectangular graph or a randomized graph with a given number " +
                        "of nodes and a degree of connectivity can be automatically created. Use [Up] and [Down] " +
                        "Arrows to adjust values.");
    }

    @Override
    public Parent getContent() {

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(0, 10, 0, 10));

        // node distance
        Label labNodeDistance = new Label("Distance between two nodes:");
        content.add(labNodeDistance, 0, 0, 1, 1);

        this.spNodeDistance = new Spinner<>(1, 1000, 250);
        content.add(this.spNodeDistance, 1, 0, 1, 1);

        this.cbNodeDistance = new ComboBox<>();
        this.cbNodeDistance.getItems().addAll(UnitPrefixes.generateUnitsForPrefixes(UnitPrefix
                .getDefaultSpacePrefixes(), METRE));
        this.cbNodeDistance.setValue(NANO(METRE));

        content.add(this.cbNodeDistance, 2, 0, 1, 1);

        // time step
        Label labTimeStep = new Label("Duration of a time step:");
        content.add(labTimeStep, 0, 1, 1, 1);

        this.spTimeStep = new Spinner<>(1, 1000, 1);
        content.add(this.spTimeStep, 1, 1, 1, 1);

        this.cbTimeStep = new ComboBox<>();
        this.cbTimeStep.getItems().addAll(UnitPrefixes.generateUnitsForPrefixes(UnitPrefix.getDefaultTimePrefixes(),
                SECOND));
        this.cbTimeStep.setValue(MICRO(SECOND));
        content.add(this.cbTimeStep, 2, 1, 1, 1);

        // temperature
        Label labTemperature = new Label("System temperature:");
        content.add(labTemperature, 0, 2, 1, 1);

        this.spTemperature = new Spinner<>(0, 100, 23, 0.1);
        content.add(this.spTemperature, 1, 2, 1, 1);

        Label labTemperatureUnit = new Label(UnitName.CELSIUS.getSymbol());
        content.add(labTemperatureUnit, 2, 2, 1, 1);

        // viscosity
        Label labViscosity = new Label("System viscosity:");
        content.add(labViscosity, 0, 3, 1, 1);

        this.spViscosity = new Spinner<>(0, 100, 1, 0.1);
        content.add(this.spViscosity, 1, 3, 1, 1);

        Label labViscosityUnit = new Label(UnitName.PASCAL.getSymbol() + UnitName.SECOND.getSymbol());
        content.add(labViscosityUnit, 2, 3, 1, 1);

        return new VBox(content);
    }

    public void createEnvironmentalVariables() {
        // TODO duplicated code in EnvironmentalParameterControlPanel
        Quantity<Length> nodeDistance = Quantities.getQuantity(this.spNodeDistance.getValue(), this.cbNodeDistance
                .getValue());
        Quantity<Time> timeStep = Quantities.getQuantity(this.spTimeStep.getValue(), this.cbTimeStep.getValue());
        Quantity<Temperature> systemTemperature = Quantities.getQuantity(this.spTemperature.getValue(),
                CELSIUS);
        Quantity<DynamicViscosity> systemViscosity = Quantities.getQuantity(this.spViscosity.getValue(),
                MILLI(PASCAL_SECOND));

        EnvironmentalParameters.getInstance().setNodeDistance(nodeDistance);
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        EnvironmentalParameters.getInstance().setSystemTemperature(systemTemperature);
        EnvironmentalParameters.getInstance().setSystemViscosity(systemViscosity);
        EnvironmentalParameters.getInstance().setCellularEnvironment(false);

    }

}
