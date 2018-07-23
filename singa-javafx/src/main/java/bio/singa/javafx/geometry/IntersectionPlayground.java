package bio.singa.javafx.geometry;

import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.Set;

/**
 * @author cl
 */
public class IntersectionPlayground extends Application implements Renderer {

    private Canvas canvas;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(500, 500);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Rectangle rectangle = new Rectangle(new Vector2D(100, 100), new Vector2D(200,200));
        strokePolygon(rectangle);

        Circle circle = new Circle(new Vector2D(200, 200), 25);
        fillCircle(circle);

        Set<Vector2D> intersections = rectangle.getIntersections(circle);
        getGraphicsContext().setLineWidth(6);
        getGraphicsContext().setFill(Color.GREEN);
        intersections.forEach(this::fillPoint);
        Iterator<Vector2D> iterator = intersections.iterator();
        Vector2D first = iterator.next();
        Vector2D second = iterator.next();

        double v = circle.getArcLengthBetween(first, second);
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
