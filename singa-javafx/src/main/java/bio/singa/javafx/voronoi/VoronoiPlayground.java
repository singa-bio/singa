package bio.singa.javafx.voronoi;

import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    private List<Vector2D> points;

    public static void main(String[] args) {
        launch();
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

        // setup button bar
        HBox buttonBar = new HBox();

        Button pointsButton = new Button("Generate points");
        pointsButton.setOnAction(this::generatePoints);

        Button voronoiButton = new Button("Generate Voronoi");
        voronoiButton.setOnAction(this::generateVoronoi);

        Button relaxButton = new Button("Relax sites");
        relaxButton.setOnAction(this::relaxSites);

        buttonBar.getChildren().addAll(pointsButton, voronoiButton, relaxButton);

        root.setBottom(buttonBar);

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
        Vector2D clickPosition = new Vector2D(event.getX(), event.getY());
        for (VoronoiCell voronoiCell : diagram.getCells()) {
            if (voronoiCell.isInside(clickPosition)) {
                getGraphicsContext().setFill(Color.INDIANRED);
                fillPolygon(voronoiCell);
                break;
            }
        }
    }

    private void generatePoints(ActionEvent event) {
        diagram = null;
        // points = Vectors.generateMultipleRandom2DVectors(50, new Rectangle(getDrawingWidth(), getDrawingHeight()));
        points = new ArrayList<>();
        points.add(new Vector2D(50, 50));
        points.add(new Vector2D(100, 50));
        points.add(new Vector2D(150, 50));
        points.add(new Vector2D(200, 50));
        clearCanvas();
        drawPoints();
    }

    private void generateVoronoi(ActionEvent event) {
        if (points != null) {
            diagram = VoronoiGenerator.generateVoronoiDiagram(points, new Rectangle(getDrawingWidth(), getDrawingHeight()));
            clearCanvas();
            drawDiagram();
            drawPoints();
        }
    }

    private void relaxSites(ActionEvent event) {
        if (points != null && diagram != null) {
            diagram = VoronoiGenerator.generateVoronoiDiagram(VoronoiRelaxation.relax(diagram), new Rectangle(getDrawingWidth(), getDrawingHeight()));
            points = diagram.getSites();
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
        getGraphicsContext().setStroke(Color.TOMATO);
        getGraphicsContext().setLineWidth(4);
        diagram.getEdges().forEach(edge -> strokeStraight(edge.getStartingPoint(), edge.getEndingPoint()));
        getGraphicsContext().setLineWidth(6);
        getGraphicsContext().setFill(Color.GREEN);
        diagram.getVertices().forEach(this::fillPoint);
    }

    private void drawPoints() {
        getGraphicsContext().setFill(Color.DARKRED);
        getGraphicsContext().setLineWidth(4);
        points.forEach(this::fillPoint);
    }

}
