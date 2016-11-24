package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphDisplayApplication extends Application {

    public static Graph graph = GraphFactory.buildTreeGraph(5 , new Rectangle(500,500));

    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphCanvas canvas = new GraphCanvas(graph);
        canvas.setWidth(500);
        canvas.setHeight(500);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        canvas.renderGraph();

        Button arrange = new Button("Arrange");
        arrange.setOnAction(canvas::arrage);
        root.setBottom(arrange);

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.arrage(new ActionEvent());
    }

    public static void main(String[] args) {
        launch();
    }

}
