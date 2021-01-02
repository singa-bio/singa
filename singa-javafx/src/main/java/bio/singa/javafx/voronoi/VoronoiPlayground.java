package bio.singa.javafx.voronoi;

import bio.singa.javafx.renderer.Renderer;
import bio.singa.javafx.renderer.graphs.GraphDisplayApplication;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 14/04/2017.
 */
public class VoronoiPlayground extends Application implements Renderer {

    private Canvas canvas;
    private VoronoiDiagram diagram;
    private Polygon boundingPolygon;
    private List<Vector2D> points;

    public static void main(String[] args) {
        GraphDisplayApplication.graph = Graphs.buildGridGraph(10, 10);
        Application.launch(VoronoiPlayground.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // test vectors
        //        vectors.add( new Vector2D( 492.10627015949353, 161.3859284431188));
        //        vectors.add( new Vector2D( 304.41390170716045, 100.8436922352427));
        //        vectors.add( new Vector2D( 227.0866297746229, 177.68042859131978));
        //        vectors.add( new Vector2D( 78.90201175867433, 222.59900136862709));
        //        vectors.add( new Vector2D( 228.7571866113683, 196.04440213246878));
        //        vectors.add( new Vector2D( 205.78320681403727, 41.06371751749649));
        //        vectors.add( new Vector2D( 108.10938219497035, 140.7761516086512));
        //        vectors.add( new Vector2D( 355.18872313636723, 358.0850591026259));
        //        vectors.add( new Vector2D( 58.6214872698857, 174.12161224688467));
        //        vectors.add( new Vector2D( 189.27987511877708, 433.229177910623));
        //        vectors.add( new Vector2D( 120.56297801688754, 208.8911052087157));
        //        vectors.add( new Vector2D( 279.1489481395522, 404.4239158747165));
        //        vectors.add( new Vector2D( 447.02634063399876, 113.29145720628742));
        //        vectors.add( new Vector2D( 433.9649857774931, 283.84419822038546));
        //        vectors.add( new Vector2D( 144.15624662658138, 412.67313559840875));
        //        vectors.add( new Vector2D( 410.67192297337454, 69.62878782584365));
        //        vectors.add( new Vector2D( 132.67889470174822, 257.62214945912297));
        //        vectors.add( new Vector2D( 336.79197335017653, 116.77048208672436));
        //        vectors.add( new Vector2D( 211.2341697614347, 241.23831556336904));
        //        vectors.add( new Vector2D( 148.00687851502775, 42.19159635139591));


        // setup root
        BorderPane root = new BorderPane();

        // setup canvas
        canvas = new Canvas(700, 700);
        canvas.setOnMouseClicked(this::handleCanvasClick);
        root.setCenter(canvas);

//        List<Vector2D> vertices = Vectors2D.generateMultipleRandom2DVectors(5, new Rectangle(500, 500));
//        boundingPolygon = new VertexPolygon(vertices);
        boundingPolygon = new Rectangle(new Vector2D(10, 10), new Vector2D(500, 500));
        generatePoints(null);
        drawBoundingPolygon();
        generateVoronoi(null);
//        ToolBar toolBar = new ToolBar();

//        Button pointsButton = new Button("Generate points");
//        pointsButton.setOnAction(this::generatePoints);
//        toolBar.getItems().add(pointsButton);
//
//        Button voronoiButton = new Button("Generate Voronoi");
//        voronoiButton.setOnAction(this::generateVoronoi);
//
//        Button relaxButton = new Button("Relax sites");
//        relaxButton.setOnAction(this::relaxSites);
//
//        buttonBar.getChildren().addAll(pointsButton, voronoiButton, relaxButton);
//
//        root.setBottom(toolBar);

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

    private void handleCanvasClick(MouseEvent event) {

        relaxSites(null);

//        Vector2D clickPosition = new Vector2D(event.getX(), event.getY());
//        for (VoronoiCell voronoiCell : diagram.getCells()) {
//            if (voronoiCell.containsVector(clickPosition)) {
//                getGraphicsContext().setFill(Color.INDIANRED);
//                fillPolygon(voronoiCell);
//                break;
//            }
//        }
    }

    private void generatePoints(ActionEvent event) {
        diagram = null;
//        points = Vectors2D.generateMultipleRandom2DVectors(10, boundingPolygon);
        points = new ArrayList<>();
        points.add(new Vector2D(100, 200));
        points.add(new Vector2D(150, 200));
        points.add(new Vector2D(200, 200));
//        points.add(new Vector2D(65.73974609375, 251.5625));
//        points.add(new Vector2D(175.1220703125, 251.5625));
//        points.add(new Vector2D(357.81982421875, 251.5625));
        clearCanvas();
        drawPoints();
    }

    private void drawBoundingPolygon() {
        strokePolygon(boundingPolygon);
    }

    private void generateVoronoi(ActionEvent event) {
        if (points != null) {
            printPoints();
            diagram = VoronoiGenerator.generateVoronoiDiagram(points, boundingPolygon);
            clearCanvas();
            drawDiagram();
            drawPoints();
        }
    }

    private void printPoints() {
        System.out.println();
        points.forEach(points -> System.out.println("points.add(new Vector2D(" + points.getX() + ", " + points.getY() + "));"));
    }

    private void relaxSites(ActionEvent event) {
        if (points != null && diagram != null) {
            points = VoronoiRelaxation.relax(diagram);
            printPoints();
            diagram = VoronoiGenerator.generateVoronoiDiagram(points, boundingPolygon);
            clearCanvas();
            drawPoints();
            drawDiagram();
        }
    }

    private void clearCanvas() {
        getGraphicsContext().setFill(Color.WHITE);
        fillRectangle(new Vector2D(0, 0), new Vector2D(getDrawingWidth(), getDrawingHeight()));
    }

    private void drawDiagram() {
        getGraphicsContext().setStroke(Color.LIGHTGRAY);
        getGraphicsContext().setLineWidth(3);
        boundingPolygon.getEdges().forEach(edge -> strokeStraight(edge.getStartingPoint(), edge.getEndingPoint()));

        getGraphicsContext().setStroke(Color.TOMATO);
        getGraphicsContext().setLineWidth(1);
        diagram.getEdges().forEach(edge -> strokeStraight(edge.getStartingPoint(), edge.getEndingPoint()));

        getGraphicsContext().setLineWidth(1);
        getGraphicsContext().setFill(Color.TOMATO);
        diagram.getVertices().forEach(this::fillPoint);
    }

    private void drawPoints() {
        getGraphicsContext().setFill(Color.GREEN);
        getGraphicsContext().setLineWidth(1);
        points.forEach(this::fillPoint);
    }

}
