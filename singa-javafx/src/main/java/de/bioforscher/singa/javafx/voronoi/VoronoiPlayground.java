package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Christoph on 14/04/2017.
 */
public class VoronoiPlayground extends Application implements Renderer {

    private Canvas canvas;
    private VoronoiDiagram voronoiDiagram;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() throws Exception {
        // setup the canvas
        this.canvas = new Canvas(500, 500);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // generate points
        List<Vector2D> vectors = Vectors.generateMultipleRandom2DVectors(20, new Rectangle(500, 500));
        // initialize voronoi diagram
        this.voronoiDiagram = new VoronoiDiagram(vectors, this.canvas);

        getGraphicsContext().setLineWidth(5.0);
        vectors.forEach(this::drawPoint);

        // setup root
        BorderPane root = new BorderPane();
        root.setCenter(this.canvas);

        Button nextEventButton = new Button("Next Event");
        nextEventButton.setOnAction(event -> this.voronoiDiagram.nextEvent());
        root.setBottom(nextEventButton);

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return this.canvas.getGraphicsContext2D();
    }

    @Override
    public double getDrawingWidth() {
        return this.canvas.getWidth();
    }

    @Override
    public double getDrawingHeight() {
        return this.canvas.getHeight();
    }

}
