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
import javafx.scene.paint.Color;
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
        // List<Vector2D> vectors = Vectors.generateMultipleRandom2DVectors(20, new Rectangle(500, 500));

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

        // {x: 200, y: 200}, {x: 50, y: 250}, {x: 400, y: 100}
        // vectors.forEach(vector -> System.out.print("{x: "+vector.getX()+", y:"+vector.getY()+"}, "));
        // initialize voronoi diagram




        // setup root
        BorderPane root = new BorderPane();
        root.setCenter(this.canvas);

        Button nextEventButton = new Button("Reinitialize");
        nextEventButton.setOnAction(event -> {
            getGraphicsContext().setFill(Color.WHITE);
            drawRectangle(new Vector2D(0,0), new Vector2D(getDrawingWidth(), getDrawingHeight()));
            List<Vector2D> vectors = Vectors.generateMultipleRandom2DVectors(20, new Rectangle(500, 500));
            getGraphicsContext().setFill(Color.BLACK);
            getGraphicsContext().setLineWidth(3);
            vectors.forEach(this::drawPoint);
            this.voronoiDiagram = new VoronoiDiagram(vectors, this.canvas);

        });
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
