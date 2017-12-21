package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author cl
 */
public class GraphDisplayApplication extends Application {

    public static Graph<? extends Node<?, Vector2D, ?>, ?, ?> graph = Graphs.buildTreeGraph(10, new Rectangle(500, 500));
    public static GraphRenderer renderer = new GraphRenderer();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Canvas canvas = new GraphCanvas(this);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        HBox buttonBar = new HBox();

        Button forceDirectedLayout = new Button("Arrange");
        forceDirectedLayout.setOnAction(action -> renderer.arrangeGraph(graph));

        Button relaxLayout = new Button("Relax");
        relaxLayout.setOnAction(action -> renderer.relaxGraph(graph));

        buttonBar.getChildren().addAll(forceDirectedLayout, relaxLayout);
        root.setBottom(buttonBar);

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        renderer.drawingWidthProperty().bind(canvas.widthProperty());
        renderer.drawingHeightProperty().bind(canvas.heightProperty());
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(buttonBar.heightProperty()));

        canvas.widthProperty().addListener(observable -> renderer.render(graph));
        canvas.heightProperty().addListener(observable -> renderer.render(graph));

    }

    public void triggerRendering() {
        renderer.render(graph);
    }

    public static GraphRenderer getRenderer() {
        return renderer;
    }

    public static Graph<? extends Node<?, Vector2D, ?>, ?, ?> getGraph() {
        return graph;
    }

}
