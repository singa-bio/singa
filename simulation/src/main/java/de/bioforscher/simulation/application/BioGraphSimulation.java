package de.bioforscher.simulation.application;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.core.utility.LogManager;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.application.components.SimulationCanvas;
import de.bioforscher.simulation.application.components.SpeciesObserverChart;
import de.bioforscher.simulation.application.windows.EnvironmentalOptionsControlPanel;
import de.bioforscher.simulation.application.windows.PlotPreferencesControlPanel;
import de.bioforscher.simulation.application.windows.SpeciesOverviewPane;
import de.bioforscher.simulation.application.wizards.AddSpeciesWizard;
import de.bioforscher.simulation.application.wizards.NewGraphWizard;
import de.bioforscher.simulation.application.wizards.NewReactionWizard;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.simulation.modules.model.Simulation;
import de.bioforscher.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.simulation.modules.reactions.model.Reactions;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.simulation.parser.GraphMLExportService;
import de.bioforscher.simulation.parser.GraphMLParserService;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.simulation.util.EnvironmentFactory;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitDictionary;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tec.units.ri.quantity.Quantities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BioGraphSimulation extends Application {

    private static final Logger log = Logger.getLogger(BioGraphSimulation.class.getName());

    private Stage stage;

    private SimulationCanvas simulationCanvas;
    private VBox chartContainer;
    private AnchorPane contextAnchor;
    private Slider concentrationSlider;

    private AutomatonGraph graph;
    private Simulation simulation;
    private List<SpeciesObserverChart> charts;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        LogManager.setDebugLevel(Level.FINE);

        log.log(Level.INFO, "setup automaton");
        // Automata
        this.simulation = new Simulation();
        this.simulation.setGraph(BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildCircularGraph(10, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)))));
        this.graph = this.simulation.getGraph();

        this.simulation.getModules().add(new FreeDiffusion());

        ChEBIParserService chebiService = new ChEBIParserService();

        // dinitrogen pentaoxide
        chebiService.setResource("CHEBI:29802");
        Species dpo = chebiService.fetchSpecies();

        // nitrogen dioxide
        chebiService.setResource("CHEBI:33101");
        Species ndo = chebiService.fetchSpecies();

        // dioxigen
        chebiService.setResource("CHEBI:15379");
        Species oxygen = chebiService.fetchSpecies();

        for (BioNode node : this.graph.getNodes()) {
            node.addEntity(dpo, 0.020);
            node.addEntity(ndo, 0);
            node.addEntity(oxygen, 0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(dpo, 1);
            edge.addPermeability(ndo, 1);
            edge.addPermeability(oxygen, 1);
        }

        // Environment
        EnvironmentFactory.createFirstOrderReactionTestEnvironment();

        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.07, UnitDictionary.PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dpo, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        this.simulation.getModules().add(reactions);
        this.simulation.getSpecies().addAll(this.simulation.collectAllReferencedEntities());
        // EnvironmentFactory.createSmallDiffusionTestEnvironment();




        // Charts
        this.charts = new ArrayList<>();

        // Stage
        this.stage = stage;
        this.stage.setTitle("GraphAutomaton Simulation");
        this.stage.setMinWidth(1200);
        this.stage.setMinHeight(800);

        // Setup the Root and Top Container
        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();

        // ContextAnchor
        this.contextAnchor = new AnchorPane();

        // Menu Bar
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu menuFile = new Menu("File");

        // New Graph
        MenuItem mINewGraph = new MenuItem("New Graph ...");
        mINewGraph.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        mINewGraph.setOnAction(this::startGraphWizard);

        // Open Graph
        MenuItem mILoadBioGraph = new MenuItem("Open BioGraph ...");
        mILoadBioGraph.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        mILoadBioGraph.setOnAction(this::loadBioGraph);

        // Save Graph
        MenuItem mISaveGraph = new MenuItem("Save BioGraph ...");
        mISaveGraph.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        mISaveGraph.setOnAction(this::saveBioGraph);

        // Adding Species
        MenuItem mIAddSpecies = new MenuItem("Add Species ...");
        mIAddSpecies.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        mIAddSpecies.setOnAction(this::showSearchSpeciesPanel);

        // Adding Reactions
        MenuItem mIAddReaction = new MenuItem("Add Reaction ...");
        mIAddReaction.setAccelerator(new KeyCodeCombination(KeyCode.R,
                KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        mIAddReaction.setOnAction(this::startReactionWizard);

        menuFile.getItems().addAll(mINewGraph, mILoadBioGraph, mISaveGraph, new SeparatorMenuItem(), mIAddSpecies,
                mIAddReaction);

        // Edit Menu
        Menu menuEdit = new Menu("Edit");

        // View Menu
        Menu menuView = new Menu("View");

        // Species Overview
        MenuItem mISpeciesOverview = new MenuItem("Species", new ImageView(IconProvider.MOLECULE_ICON_IMAGE));
        mISpeciesOverview.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN));
        mISpeciesOverview.setOnAction(this::showSpeciesOverview);

        menuView.getItems().addAll(mISpeciesOverview);

        Menu menuPreferences = new Menu("Preferences");

        MenuItem mIPlot = new MenuItem("Plot preferences");
        mIPlot.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN));
        mIPlot.setOnAction(this::showPlotPreferencesControlPanel);

        menuPreferences.getItems().add(mIPlot);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuPreferences);

        // Simulation Half
        this.simulationCanvas = new SimulationCanvas(this);

        // Chart Half
        final TabPane rightPane = new TabPane();
        this.chartContainer = new VBox();

        Tab chartTab = new Tab();
        chartTab.setText("Plots");
        chartTab.setClosable(false);
        chartTab.setContent(this.chartContainer);
        rightPane.getTabs().add(chartTab);

        Tab environmentTab = new Tab();
        environmentTab.setText("Environment");
        environmentTab.setClosable(false);
        EnvironmentalOptionsControlPanel environmentControlPanel = new EnvironmentalOptionsControlPanel(environmentTab);
        environmentControlPanel.update(EnvironmentalVariables.getInstance(), null);
        environmentTab.setContent(environmentControlPanel);
        rightPane.getTabs().add(environmentTab);

        // Main Content Pane
        SplitPane splitPane = new SplitPane(this.simulationCanvas, rightPane);

        // ToolBar
        ToolBar toolBar = new ToolBar();
        // Simulate Button
        Button btnSimulate = new Button();
        btnSimulate.setId("btnSimulate");
        btnSimulate.setPrefSize(40, 40);
        btnSimulate.setOnAction(this::startSimulation);

        // Rearrange Button
        Button btnRearrange = new Button();
        btnRearrange.setId("btnRearrange");
        btnRearrange.setPrefSize(40, 40);
        btnRearrange.setOnAction(this.simulationCanvas::arrangeGraph);

        // Concentration Slider
        setupConcentrationSlider();

        // Add toolbar components
        toolBar.getItems().addAll(btnSimulate, btnRearrange, this.concentrationSlider);

        // Add Toolbar and Menu
        topContainer.getChildren().addAll(menuBar, toolBar);
        root.setTop(topContainer);

        // Simulation Frame
        root.setCenter(splitPane);

        // Anchor to the Bottom
        root.setBottom(this.contextAnchor);

        // Scene
        Scene scene = new Scene(root);
        // TODO Fix this
        File f = new File("D:/projects/simulation/target/classes/application.css");
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        // scene.getStylesheets().addAll(this.getClass().getResource("application.css").toExternalForm());
        stage.setScene(scene);

        // Show
        stage.show();
        this.simulationCanvas.draw();
    }

    public Stage prepareUtilityWindow(int width, int height, String title) {
        Stage utilityStage = new Stage();
        utilityStage.initModality(Modality.APPLICATION_MODAL);
        utilityStage.initStyle(StageStyle.UTILITY);
        utilityStage.setTitle(title);
        utilityStage.setX(this.stage.getX() + this.stage.getWidth() / 2 - width / 2);
        utilityStage.setY(this.stage.getY() + this.stage.getHeight() / 2 - height / 2);
        return utilityStage;
    }

    public void showSearchSpeciesPanel(ActionEvent event) {
        int width = 600;
        int height = 530;
        Stage speciesStage = prepareUtilityWindow(width, height, "Search Species...");
        AddSpeciesWizard speciesWizard = new AddSpeciesWizard(speciesStage, this);
        speciesStage.setScene(new Scene(speciesWizard, width, height));
        speciesStage.sizeToScene();
        speciesStage.showAndWait();
        if (speciesWizard.getSpeciesToAdd() != null) {
            speciesWizard.getSpeciesToAdd().forEach(species -> {
                this.simulation.getSpecies().add(species);
                this.simulationCanvas.resetGraphContextMenu();
            });
        }
    }

    public void showSpeciesOverview(ActionEvent event) {
        int width = 800;
        int height = 600;
        Stage speciesStage = prepareUtilityWindow(width, height, "Species Overview");
        SpeciesOverviewPane speciesOverviewPane = new SpeciesOverviewPane(this);
        speciesStage.setScene(new Scene(speciesOverviewPane, width, height));
        speciesStage.sizeToScene();
        speciesStage.showAndWait();
    }

    public void startGraphWizard(ActionEvent event) {
        int width = 600;
        int height = 300;
        Stage graphStage = prepareUtilityWindow(width, height, "New Graph Wizard");
        NewGraphWizard graphWizard = new NewGraphWizard(graphStage);
        graphStage.setScene(new Scene(graphWizard, width, height));
        graphStage.showAndWait();
        if (graphWizard.getGraph() != null) {
            resetGraph(graphWizard.getGraph());
        }
    }

    public void startReactionWizard(ActionEvent event) {
        int width = 800;
        int height = 600;
        Stage reactionStage = prepareUtilityWindow(width, height, "New Reaction Wizard");
        NewReactionWizard reactionWizard = new NewReactionWizard(reactionStage);
        reactionStage.setScene(new Scene(reactionWizard, width, height));
        reactionStage.showAndWait();
        if (reactionWizard.getReaction() != null) {
            // TODO migrate to new interface and reaction interface
            // this.simulation.addReaction(reactionWizard.getReaction(), true);
            // redrawGraph();
        }
    }

    public void showPlotPreferencesControlPanel(ActionEvent event) {
        int width = 400;
        int height = 300;
        Stage plotPreferencesStage = prepareUtilityWindow(width, height, "Plot preferences");
        PlotPreferencesControlPanel plotPreferencesControlPanel = new PlotPreferencesControlPanel(plotPreferencesStage);
        plotPreferencesStage.setScene(new Scene(plotPreferencesControlPanel));
        plotPreferencesStage.showAndWait();
    }

    public FileChooser prepareFileChooser(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        for (String extension : extensions) {
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    extension.toUpperCase() + " files (*." + extension + ")", "*." + extension);
            fileChooser.getExtensionFilters().add(extFilter);
        }
        return fileChooser;
    }

    public void loadBioGraph(ActionEvent event) {
        FileChooser fileChooser = prepareFileChooser("Load GraphML-File", "xml");
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            GraphMLParserService parserService = new GraphMLParserService(file.getPath());
            AutomatonGraph graph = parserService.fetchGraph();
            resetGraph(graph);
        }
    }

    public void saveBioGraph(ActionEvent event) {
        FileChooser fileChooser = prepareFileChooser("Save graph to file", "xml");
        File file = fileChooser.showSaveDialog(this.stage);
        if (file != null) {
            GraphMLExportService.exportGraph(this.graph, file);
        }
    }

    public void startSimulation(ActionEvent event) {
        prepareObserverCharts();
        // TODO revert this
        // this.automata.activateWriteObservedNodesToFiles();
        this.simulationCanvas.startSimulation();
    }

    public void prepareObserverCharts() {
        for (SpeciesObserverChart chart : this.charts) {
            this.simulation.addEventListener(chart);
        }
    }

    public void setupConcentrationSlider() {
        this.concentrationSlider = new Slider();
        this.concentrationSlider.setMin(0);
        this.concentrationSlider.setMax(1);
        this.concentrationSlider.setValue(1);
        this.concentrationSlider.setShowTickLabels(true);
        this.concentrationSlider.setShowTickMarks(true);
        this.concentrationSlider.setMajorTickUnit(0.5);
        this.concentrationSlider.setMinorTickCount(4);
    }

    public void resetGraph(AutomatonGraph graph) {
        this.graph = graph;
        this.simulation.setGraph(this.graph);
        this.simulationCanvas.getRenderer().getBioRenderingOptions().setNodeHighlightSpecies(null);
        this.simulationCanvas.getRenderer().getBioRenderingOptions().setEdgeHighlightSpecies(null);
        this.simulationCanvas.resetGraphContextMenu();
        this.simulationCanvas.draw();
    }

    public void resetGraphContextMenu() {
        this.simulationCanvas.resetGraphContextMenu();
    }

    public void redrawGraph() {
        this.graph = this.simulation.getGraph();
        this.simulationCanvas.getRenderer().getBioRenderingOptions().setNodeHighlightSpecies(null);
        this.simulationCanvas.getRenderer().getBioRenderingOptions().setEdgeHighlightSpecies(null);
        this.simulationCanvas.draw();
    }

    public VBox getChartContainer() {
        return this.chartContainer;
    }

    public void setChartContainer(VBox chartContainer) {
        this.chartContainer = chartContainer;
    }

    public AnchorPane getContextAnchor() {
        return this.contextAnchor;
    }

    public void setContextAnchor(AnchorPane contextAnchor) {
        this.contextAnchor = contextAnchor;
    }

    public Slider getConcentrationSlider() {
        return this.concentrationSlider;
    }

    public void setConcentrationSlider(Slider concentrationSlider) {
        this.concentrationSlider = concentrationSlider;
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public List<SpeciesObserverChart> getCharts() {
        return this.charts;
    }

    public void setCharts(List<SpeciesObserverChart> charts) {
        this.charts = charts;
    }

}
