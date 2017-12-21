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
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas();
        canvas.setWidth(1400);
        canvas.setHeight(1400);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Button arrange = new Button("Arrange");
        arrange.setOnAction(action -> renderer.arrangeGraph(graph));
        root.setBottom(arrange);

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        renderer.drawingWidthProperty().bind(canvas.widthProperty());
        renderer.drawingHeightProperty().bind(canvas.heightProperty());
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());
        renderer.arrangeGraph(graph);
    }

}
