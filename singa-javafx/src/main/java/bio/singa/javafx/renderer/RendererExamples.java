package bio.singa.javafx.renderer;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.Parabola;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author cl
 */
public class RendererExamples extends Application implements Renderer {

    private Canvas canvas;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(500, 500);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        // add axis
        getGraphicsContext().setLineWidth(5);
        getGraphicsContext().setStroke(Color.BLACK);

        Line xAxis = new Line(0, 0);
        Line yAxis = new Line(0, Double.POSITIVE_INFINITY);

        strokeLine(xAxis);
        strokeLine(yAxis);

        getGraphicsContext().setStroke(Color.INDIANRED);
        Vector2D focus = new Vector2D(150, 70);
        fillPoint(focus);

        getGraphicsContext().setLineWidth(2);
        getGraphicsContext().setFill(Color.CORAL);
        Line directrix = new Line(50, 0);
        strokeLine(directrix);

        Parabola parabola = new Parabola(focus, directrix);
        strokeParabola(parabola, 30);

        Line randomLine = new Line(new Vector2D(140, 60), Double.POSITIVE_INFINITY);
        getGraphicsContext().setStroke(Color.DARKGOLDENROD);
        strokeLine(randomLine);
        getGraphicsContext().setLineWidth(5);
        getGraphicsContext().setFill(Color.BROWN);
        parabola.getIntercepts(randomLine).forEach(this::fillPoint);

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
