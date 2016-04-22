package de.bioforscher.simulation.application.wizards;

import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.simulation.application.components.SimulationSpace;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.quantities.DynamicViscosity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;

import static de.bioforscher.units.UnitDictionary.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.*;

/**
 * A wizard used to create a new graph.
 *
 * @author Christoph Leberecht
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
    private ToggleGroup tgMethods = new ToggleGroup();

    public GraphConfigurationPage() {
        super("Configure Graph");
        setDescription(
                "A new graph will be created. A regular rectangular graph or a randomized graph with a given number of nodes and a degree of conectivity can be automatically created. Use [Up] and [Down] Arrows to adjust values.");

        this.rbRectangularGraph.setToggleGroup(this.tgMethods);
        this.rbRandomizedGraph.setToggleGroup(this.tgMethods);

        this.nextButton.setDisable(true);
        this.finishButton.setDisable(true);

        this.spNumberHorizontalNodes.setDisable(true);
        this.spNumberVerticalNodes.setDisable(true);
        this.spNumberNodes.setDisable(true);
        this.spConnectivity.setDisable(true);

        this.tgMethods.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {

                GraphConfigurationPage.this.nextButton.setDisable(false);
                GraphConfigurationPage.this.finishButton.setDisable(false);

                if (newToggle.getUserData().equals("rectangle")) {
                    GraphConfigurationPage.this.spNumberHorizontalNodes.setDisable(false);
                    GraphConfigurationPage.this.spNumberVerticalNodes.setDisable(false);
                    GraphConfigurationPage.this.spNumberNodes.setDisable(true);
                    GraphConfigurationPage.this.spConnectivity.setDisable(true);
                } else if (newToggle.getUserData().equals("randomized")) {
                    GraphConfigurationPage.this.spNumberHorizontalNodes.setDisable(true);
                    GraphConfigurationPage.this.spNumberVerticalNodes.setDisable(true);
                    GraphConfigurationPage.this.spNumberNodes.setDisable(false);
                    GraphConfigurationPage.this.spConnectivity.setDisable(false);
                }
            }
        });

    }

    @Override
    public Parent getContent() {

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(0, 10, 0, 10));

        // rectangular graph
        this.rbRectangularGraph = new RadioButton("Create rectangular Graph.");
        this.rbRectangularGraph.setUserData("rectangle");
        content.add(this.rbRectangularGraph, 0, 0, 4, 1);

        Label labHorizontalNodes = new Label("Width (Number of Nodes):");
        content.add(labHorizontalNodes, 0, 1, 1, 1);

        this.spNumberHorizontalNodes = new Spinner<Integer>(1, 100, 10);
        content.add(this.spNumberHorizontalNodes, 1, 1, 1, 1);

        Label labVerticalNodes = new Label("Height (Number of Nodes):");
        content.add(labVerticalNodes, 2, 1, 1, 1);

        this.spNumberVerticalNodes = new Spinner<Integer>(1, 100, 10);
        content.add(this.spNumberVerticalNodes, 3, 1, 1, 1);

        // randomized graph
        this.rbRandomizedGraph = new RadioButton("Create randomized Graph (Erdos�Renyi model).");
        this.rbRandomizedGraph.setUserData("randomized");
        content.add(this.rbRandomizedGraph, 0, 2, 4, 1);

        Label labNumberNodes = new Label("Number of Nodes: ");
        content.add(labNumberNodes, 0, 3, 1, 1);

        this.spNumberNodes = new Spinner<Integer>(1, 10000, 50);
        content.add(this.spNumberNodes, 1, 3, 1, 1);

        Label labConnectivity = new Label("Connectivity in %: ");
        content.add(labConnectivity, 2, 3, 1, 1);

        this.spConnectivity = new Spinner<Double>(0, 1, 0.1, 0.01);
        content.add(this.spConnectivity, 3, 3, 1, 1);

        return new VBox(content);
    }

    public AutomatonGraph createGraph() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbRectangularGraph)) {
            return BioGraphUtilities.castUndirectedGraphToBioGraph(GraphFactory.buildGridGraph(
                    this.spNumberVerticalNodes.getValue(), this.spNumberHorizontalNodes.getValue(),
                    SimulationSpace.getInstance().getRectangle(), false));
        } else {
            return BioGraphUtilities
                    .castUndirectedGraphToBioGraph(GraphFactory.buildRandomGraph(this.spNumberNodes.getValue(),
                            this.spConnectivity.getValue(), SimulationSpace.getInstance().getRectangle()));
        }
    }

    @Override
    public void nextPage() {
        ((NewGraphWizard) getWizard()).setGraph(createGraph());
        super.nextPage();
    }
}

class EnvironmentalConfigurationPage extends WizardPage {

    private Spinner<Integer> spNodeDistance;
    private Spinner<Integer> spTimeStep;
    private Spinner<Double> spTemperature;
    private Spinner<Double> spViscosity;

    private ComboBox<String> cbNodeDistance;
    private ComboBox<String> cbTimeStep;

    public EnvironmentalConfigurationPage() {
        super("Environmental Options");
        setDescription(
                "A new graph will be created. A regular rectangular graph or a randomized graph with a given number of nodes and a degree of conectivity can be automatically created. Use [Up] and [Down] Arrows to adjust values.");
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

        this.spNodeDistance = new Spinner<Integer>(1, 1000, 250);
        content.add(this.spNodeDistance, 1, 0, 1, 1);

        this.cbNodeDistance = new ComboBox<String>();
        this.cbNodeDistance.getItems().addAll("nm", "�m", "mm");
        this.cbNodeDistance.setValue("nm");
        content.add(this.cbNodeDistance, 2, 0, 1, 1);

        // time step
        Label labTimeStep = new Label("Duration of a time step:");
        content.add(labTimeStep, 0, 1, 1, 1);

        this.spTimeStep = new Spinner<Integer>(1, 1000, 1);
        content.add(this.spTimeStep, 1, 1, 1, 1);

        this.cbTimeStep = new ComboBox<String>();
        this.cbTimeStep.getItems().addAll("ns", "�s", "ms", "s");
        this.cbTimeStep.setValue("�s");
        content.add(this.cbTimeStep, 2, 1, 1, 1);

        // temperature
        Label labTemperature = new Label("System temperature:");
        content.add(labTemperature, 0, 2, 1, 1);

        this.spTemperature = new Spinner<Double>(0, 100, 23, 0.1);
        content.add(this.spTemperature, 1, 2, 1, 1);

        Label labTemperatureUnit = new Label("�C");
        content.add(labTemperatureUnit, 2, 2, 1, 1);

        // viscosity
        Label labViscosity = new Label("System viscosity:");
        content.add(labViscosity, 0, 3, 1, 1);

        this.spViscosity = new Spinner<Double>(0, 100, 1, 0.1);
        content.add(this.spViscosity, 1, 3, 1, 1);

        Label labViscosityUnit = new Label("mPs");
        content.add(labViscosityUnit, 2, 3, 1, 1);

        return new VBox(content);
    }

    public void createEnvironmentalVariables() {
        // TODO duplicated code
        double nodeDistanceValue = this.spNodeDistance.getValue();
        Quantity<Length> nodeDistance = null;
        switch (this.cbNodeDistance.getValue()) {
            case "nm":
                nodeDistance = Quantities.getQuantity(nodeDistanceValue, NANO(METRE));
                break;
            case "�m":
                nodeDistance = Quantities.getQuantity(nodeDistanceValue, MICRO(METRE));
                break;
            case "mm":
                nodeDistance = Quantities.getQuantity(nodeDistanceValue, MILLI(METRE));
                break;
        }

        double timeStepValue = this.spTimeStep.getValue();
        Quantity<Time> timeStep = null;
        switch (this.cbTimeStep.getValue()) {
            case "ns":
                timeStep = Quantities.getQuantity(timeStepValue, NANO(SECOND));
                break;
            case "�s":
                timeStep = Quantities.getQuantity(timeStepValue, MICRO(SECOND));
                break;
            case "ms":
                timeStep = Quantities.getQuantity(timeStepValue, MILLI(SECOND));
                break;
            case "s":
                timeStep = Quantities.getQuantity(timeStepValue, SECOND);
                break;
        }

        Quantity<Temperature> systemTemperature = Quantities.getQuantity((double) this.spTemperature.getValue(),
                CELSIUS);

        Quantity<DynamicViscosity> systemViscosity = Quantities.getQuantity((double) this.spViscosity.getValue(),
                MILLI(PASCAL_SECOND));

        EnvironmentalVariables.getInstance().setNodeDistance(nodeDistance);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);
        EnvironmentalVariables.getInstance().setSystemTemperature(systemTemperature);
        EnvironmentalVariables.getInstance().setSystemViscosity(systemViscosity);
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);

    }

    @Override
    public void nextPage() {
        super.nextPage();
    }
}
