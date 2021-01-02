package bio.singa.javafx.renderer;

import bio.singa.mathematics.geometry.edges.LineRay;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Circles;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Set;

/**
 * @author cl
 */
public class IntersectionRenderer extends Application implements Renderer {

    private Canvas canvas;
    private SimpleLineSegment ls;
    private LineRay ray;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(700, 700);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleDrag);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDrag);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleDrag);
        getGraphicsContext().setLineWidth(3);
        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        // total height of box
//        ls = new SimpleLineSegment(new Vector2D(70, 50), new Vector2D(190, 230));
//        strokeLineSegment(ls);

        getGraphicsContext().setStroke(Color.GREEN);
        ray = new LineRay(new Vector2D(80, 80), Vectors2D.generateStandardGaussian2DVector());
        strokeRay(ray);
        getGraphicsContext().setStroke(Color.RED);
        Polygon poly = new Rectangle(new Vector2D(100, 100), new Vector2D(200, 200));
        strokePolygon(poly);
        getGraphicsContext().setFill(Color.BLUE);
        Set<Vector2D> intersections = poly.getIntersections(ray);
        for (Vector2D intersection : intersections) {
            fillPoint(intersection);
        }

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void handleDrag(MouseEvent event) {
        getGraphicsContext().setFill(Color.WHITE);
        getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        // drag moves node
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            getGraphicsContext().setStroke(Color.RED);
            Vector2D dragStart = new Vector2D(event.getX(), event.getY());
            Circle circle = new Circle(dragStart, 20);
            strokeCircle(circle);
            if (Circles.intersect(circle, ls)) {
                getGraphicsContext().setStroke(Color.RED);
            } else {
                getGraphicsContext().setStroke(Color.BLACK);
            }

            strokeLineSegment(ls);
        }
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
