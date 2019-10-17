package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static bio.singa.javafx.renderer.graphs.GraphRenderer.RenderingMode;

/**
 * @author cl
 */
public class GraphDisplayApplication extends Application {

    public static Graph<? extends Node<?, Vector2D, ?>, ?, ?> graph = Graphs.buildGridGraph(5, 5);
    public static GraphRenderer renderer = new GraphRenderer();

    public static void main(String[] args) {
        launch();
    }

    public static GraphRenderer getRenderer() {
        return renderer;
    }

    public static Graph<? extends Node<?, Vector2D, ?>, ?, ?> getGraph() {
        return graph;
    }

    @Override
    public void start(Stage primaryStage) {
        // root pane
        BorderPane root = new BorderPane();

        // top part
        VBox topContainer = new VBox();
        final MenuBar menuBar = prepareMenus();
        final ToolBar toolBar = prepareViewingToolBar();
        topContainer.getChildren().addAll(menuBar, toolBar);
        root.setTop(topContainer);

        // center part
        Canvas canvas = new GraphCanvas();
        renderer.renderVoronoi(false);
        root.setCenter(canvas);

        // get size of stage
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setWidth(primaryScreenBounds.getWidth() / 2.0);
        primaryStage.setHeight(primaryScreenBounds.getHeight() / 2.0);

        // show
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        renderer.drawingWidthProperty().bind(canvas.widthProperty());
        renderer.drawingHeightProperty().bind(canvas.heightProperty());
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(topContainer.heightProperty()));

        canvas.widthProperty().addListener(observable -> renderer.render(graph));
        canvas.heightProperty().addListener(observable -> renderer.render(graph));

    }

    private ToolBar prepareViewingToolBar() {
        ToolBar toolBar = new ToolBar();
        // center button
        Button center = new Button("Center");
        center.setOnAction(action -> renderer.centerGraph(graph));
        // arrange button
        Button forceDirectedLayout = new Button("Arrange");
        forceDirectedLayout.setOnAction(action -> renderer.arrangeGraph(graph));
        // relax button
        Button relaxLayout = new Button("Relax");
        relaxLayout.setOnAction(action -> renderer.relaxGraph(graph));
        // add items to toolbar
        toolBar.getItems().addAll(center, forceDirectedLayout, relaxLayout);
        return toolBar;
    }

    private MenuBar prepareMenus() {
        MenuBar menuBar = new MenuBar();

        // file menu
        Menu menuFile = new Menu("File");
        // new graph
        MenuItem mINewGraph = new MenuItem("New graph ...");
        mINewGraph.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        // mINewGraph.setOnAction(this::startGraphWizard);
        // open Graph
        MenuItem mILoadBioGraph = new MenuItem("Open graph ...");
        mILoadBioGraph.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        // mILoadBioGraph.setOnAction(this::loadBioGraph);
        // save Graph
        MenuItem mISaveGraph = new MenuItem("Save graph ...");
        mISaveGraph.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        // mISaveGraph.setOnAction(this::saveBioGraph);

        // rendering menu
        Menu menuRendering = new Menu("Rendering");
        // rendering mode
        Menu menuRenderingMode = new Menu("Rendering mode");
        final ToggleGroup groupRenderingMode = new ToggleGroup();
        for (RenderingMode renderingMode : RenderingMode.values()) {
            RadioMenuItem itemMode = new RadioMenuItem(renderingMode.getDispayText());
            itemMode.setUserData(renderingMode);
            itemMode.setToggleGroup(groupRenderingMode);
            menuRenderingMode.getItems().add(itemMode);
            if (renderingMode.name().equals(renderer.getRenderingMode())) {
                itemMode.setSelected(true);
            }
        }
        groupRenderingMode.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (groupRenderingMode.getSelectedToggle() != null) {
                renderer.setRenderingMode(((RenderingMode) groupRenderingMode.getSelectedToggle().getUserData()).name());
            }
        });
        // voronoi drawing
        CheckMenuItem voronoiItem = new CheckMenuItem("Render Voronoi");
        voronoiItem.setSelected(true);
        voronoiItem.selectedProperty().addListener((ov, old_val, new_val) -> {
            renderer.renderVoronoi(new_val);
            renderer.render(graph);
        });
        // add rendering items
        menuRendering.getItems().addAll(menuRenderingMode, voronoiItem);

        // add items to file menu
        menuFile.getItems().addAll(mINewGraph, mILoadBioGraph, mISaveGraph);
        // add menus to menu bar
        menuBar.getMenus().addAll(menuFile, menuRendering);
        return menuBar;
    }

}
