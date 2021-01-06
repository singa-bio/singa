package bio.singa.javafx.voronoi;

import bio.singa.javafx.renderer.Renderer;
import bio.singa.javafx.renderer.graphs.GraphDisplayApplication;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiHalfEdge;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors2D;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
//        points.add(new Vector2D(74.52592345749316, 77.42901243562156));
//        points.add(new Vector2D(438.78698728924513, 90.87280755881734));
//        points.add(new Vector2D(206.481158819044, 95.37541880005496));
//        points.add(new Vector2D(320.92307542598354, 118.67345818160646));
//        points.add(new Vector2D(98.63945819500807, 215.11513459793625));
//        points.add(new Vector2D(423.8559086624602, 267.47397764948494));
//        points.add(new Vector2D(255.94317793773138, 298.6865263299364));
//        points.add(new Vector2D(74.29743058838064, 356.94979734705805));
//        points.add(new Vector2D(391.5833653119365, 428.32827533909676));
//        points.add(new Vector2D(163.43925271445042, 445.871635186965));



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
        points = Vectors2D.generateMultipleRandom2DVectors(10, boundingPolygon);
//        points = new ArrayList<>();
//        points.add(new Vector2D(100, 200));
//        points.add(new Vector2D(150, 200));
//        points.add(new Vector2D(200, 200));
//        points.add(new Vector2D(82.3185325867044, 254.99832153320312));
//        points.add(new Vector2D(236.11033419183457, 254.99832153320312));
//        points.add(new Vector2D(408.7885972594271, 254.99832153320318));
        clearCanvas();
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

        getGraphicsContext().setLineWidth(1);

        getGraphicsContext().setFont(Font.font(8));
        for (VoronoiCell cell : diagram.getCells()) {
        int i = 0;
            Color randomColor = Color.color(Math.random(), Math.random(), Math.random());
            getGraphicsContext().setStroke(randomColor);
            getGraphicsContext().setFill(randomColor);
            for (VoronoiHalfEdge edge : cell.getHalfEdges()) {
                Vector2D innerSite = cell.getSite().getSite();
                Vector2D edgeStartingPoint = edge.getStartPoint();
                Vector2D edgeEndingPoint = edge.getEndPoint();
                Vector2D edgeMidpoint = edgeStartingPoint.getMidpointTo(edgeEndingPoint);
                Vector2D innerDirection = innerSite.subtract(edgeMidpoint).normalize();
                strokeLineSegmentWithArrow(edgeStartingPoint, edgeEndingPoint);
                fillPoint(innerSite);
                strokeTextCenteredOnPoint(String.valueOf(i), edgeMidpoint.add(innerDirection.multiply(10)));
                i++;
            }
        }

    }

}
