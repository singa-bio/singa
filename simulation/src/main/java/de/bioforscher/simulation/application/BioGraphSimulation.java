package de.bioforscher.simulation.application;

import de.bioforscher.core.utility.LogManager;
import de.bioforscher.simulation.application.components.EnvironmentalOptionsControlPanel;
import de.bioforscher.simulation.application.components.SimulationCanvas;
import de.bioforscher.simulation.application.components.plots.PlotCard;
import de.bioforscher.simulation.application.components.plots.PlotPane;
import de.bioforscher.simulation.application.components.plots.PlotPreferencesControlPanel;
import de.bioforscher.simulation.application.components.species.SpeciesOverviewPane;
import de.bioforscher.simulation.application.wizards.AddSpeciesWizard;
import de.bioforscher.simulation.application.wizards.NewGraphWizard;
import de.bioforscher.simulation.application.wizards.NewReactionWizard;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.modules.model.Simulation;
import de.bioforscher.simulation.parser.GraphMLExportService;
import de.bioforscher.simulation.parser.GraphMLParserService;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.simulation.util.SimulationExampleProvider;
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
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BioGraphSimulation extends Application {

    private static final Logger log = Logger.getLogger(BioGraphSimulation.class.getName());

    private Stage stage;

    private SimulationCanvas simulationCanvas;
    private PlotPane chartContainer;
    private AnchorPane contextAnchor;
    private Slider concentrationSlider;

    private AutomatonGraph graph;
    private Simulation simulation;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        LogManager.setDebugLevel(Level.FINE);

        log.log(Level.INFO, "setup automaton");
        // setup the simulation
        this.simulation = SimulationExampleProvider.createDiffusionModuleExample(10, Quantities.getQuantity(250,
                MetricPrefix.NANO(Units.SECOND)));
        // this.simulation = SimulationExampleProvider.createIodineMultiReactionExample();
        this.graph = this.simulation.getGraph();
        // Charts

        // Stage
        this.stage = stage;
        this.stage.setMaximized(true);
        this.stage.setTitle("GraphAutomaton Simulation");

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

        // Full screen toggle
        MenuItem mIFullScreen = new MenuItem("Toggle full screen");
        mIFullScreen.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        mIFullScreen.setOnAction(this::toggleFullScreen);

        // Species Overview
        MenuItem mISpeciesOverview = new MenuItem("Species", new ImageView(IconProvider.MOLECULE_ICON_IMAGE));
        mISpeciesOverview.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN));
        mISpeciesOverview.setOnAction(this::showSpeciesOverview);

        menuView.getItems().addAll(mIFullScreen, mISpeciesOverview);

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
        this.chartContainer = new PlotPane(this);

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
        Button btnSimulate = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_PLAY);
        btnSimulate.setTooltip(new Tooltip("Starts the simulation with the current configuration."));
        btnSimulate.setOnAction(this::startSimulation);
        // Rearrange sutton
        Button btnRearrange = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_EXCHANGE);
        btnRearrange.setTooltip(new Tooltip("Starts a rearrangement cycle trying to optimize the graph layout."));
        btnRearrange.setOnAction(this.simulationCanvas::arrangeGraph);
        // Concentration slider
        setupConcentrationSlider();

        // Add toolbar components
        toolBar.getItems().addAll(btnSimulate, btnRearrange, this.concentrationSlider);

        // Add toolbar and menu
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
        for (PlotCard chart : this.chartContainer.getPlotCards().getItems()) {
            this.simulation.addEventListener(chart.getPlot());
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
        // this.simulationCanvas.getRenderer().getBioRenderingOptions().setNodeHighlightSpecies(null);
        // this.simulationCanvas.getRenderer().getBioRenderingOptions().setEdgeHighlightSpecies(null);
        this.simulationCanvas.draw();
    }

    private void toggleFullScreen(ActionEvent event) {
        if (this.stage.isFullScreen()) {
            this.stage.setFullScreen(false);
        } else {
            this.stage.setFullScreen(true);
        }
    }

    public PlotPane getChartContainer() {
        return this.chartContainer;
    }

    public void setChartContainer(PlotPane chartContainer) {
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

}
