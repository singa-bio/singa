package de.bioforscher.singa.javafx.geometry;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.algorithms.geometry.ConvexHull;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Christoph on 14/04/2017.
 */
public class ConvexHullPlayground extends Application implements Renderer {

    private Canvas canvas;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() throws Exception {
        // setup the canvas
        canvas = new Canvas(500, 500);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // setup root
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        canvas.getGraphicsContext2D().setLineWidth(7);

        // generate points
        List<Vector2D> vectors = Vectors.generateMultipleRandom2DVectors(20, new Rectangle(500, 500));
        ConvexHull convexHull = ConvexHull.calculateHullFor(vectors);

        canvas.getGraphicsContext2D().setFill(Color.DIMGRAY);
        convexHull.getNonHullVectors().forEach(this::drawPoint);

        canvas.getGraphicsContext2D().setFill(Color.CORNFLOWERBLUE);
        convexHull.getHull().forEach(this::drawPoint);

        canvas.getGraphicsContext2D().setStroke(Color.CORNFLOWERBLUE);
        canvas.getGraphicsContext2D().setLineWidth(2);
        connectPoints(convexHull.getHull());

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return canvas.getGraphicsContext2D();
    }

    @Override
    public double getDrawingWidth() {
        return canvas.getWidth();
    }

    @Override
    public double getDrawingHeight() {
        return canvas.getHeight();
    }

}
