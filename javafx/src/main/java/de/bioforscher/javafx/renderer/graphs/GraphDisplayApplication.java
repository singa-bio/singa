package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
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

    public static Graph graph = GraphFactory.buildTreeGraph(10 , new Rectangle(500,500));
    public static GraphRenderer renderer = new GraphRenderer();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Canvas  canvas = new Canvas();
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

    public static void main(String[] args) {
        launch();
    }

}
