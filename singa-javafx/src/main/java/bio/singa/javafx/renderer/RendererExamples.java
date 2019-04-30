package bio.singa.javafx.renderer;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

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

        canvas = new Canvas(700, 700);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        // add axis
        getGraphicsContext().setLineWidth(5);
        getGraphicsContext().setStroke(Color.BLACK);

        Line xAxis = new Line(0, 0);
        Line yAxis = new Line(0, Double.POSITIVE_INFINITY);

        Rectangle r1 = new Rectangle(new Vector2D(500, 500), new Vector2D(600, 600));
        Polygon r2 = new ComplexPolygon(new Vector2D(550.0, 400.0), new Vector2D(550.0, 500.0), new Vector2D(500.0, 500.0), new Vector2D(500.0, 400.0));

        strokePolygon(r1);
        strokePolygon(r2);
        getGraphicsContext().setStroke(Color.RED);
        Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(r1, r2);
        LineSegment segment = touchingLineSegments.values().iterator().next();
        strokeLineSegment(segment);

        // strokeLine(xAxis);
        // strokeLine(yAxis);

//        getGraphicsContext().setStroke(Color.INDIANRED);
//        Vector2D focus = new Vector2D(150, 70);
//        fillPoint(focus);
//
//        getGraphicsContext().setLineWidth(2);
//        getGraphicsContext().setFill(Color.CORAL);
//        Line directrix = new Line(50, 0);
//        strokeLine(directrix);
//
//        Parabola parabola = new Parabola(focus, directrix);
//        strokeParabola(parabola, 30);
//
//        Line randomLine = new Line(new Vector2D(140, 60), Double.POSITIVE_INFINITY);
//        getGraphicsContext().setStroke(Color.DARKGOLDENROD);
//        strokeLine(randomLine);
//        getGraphicsContext().setLineWidth(5);
//        getGraphicsContext().setFill(Color.BROWN);
//        parabola.getIntercepts(randomLine).forEach(this::fillPoint);

//        SimpleLineSegment edge = new SimpleLineSegment(new Vector2D(47.603305785123965, 447.2727272727273), new Vector2D(41.98347107438017, 435.702479338843));
//        SimpleLineSegment points = new SimpleLineSegment(new Vector2D(0.0, 363.6363636363637), new Vector2D(0.0, 327.2727272727273));

//        strokeLineSegment(edge);
//        strokeLineSegment(points);


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
