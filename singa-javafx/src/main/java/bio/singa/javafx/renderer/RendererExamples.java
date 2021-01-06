package bio.singa.javafx.renderer;

import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
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

        canvas = new Canvas(700, 700);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        // arrows

        strokeLineSegmentWithArrow(new Vector2D(10,10), new Vector2D(200, 200));







        // MSA
//        // set font
//        Font arial = Font.font("arial", FontWeight.LIGHT, FontPosture.REGULAR, 30);
//        getGraphicsContext().setFont(arial);
//        // set starting position
//        double previousYPosition = 100;
//        double totalHeight = 0;
//
//        // for each letter
//        // determine height of letter
//        String letter = "A";
//        Text text = new Text(letter);
//        text.setFont(arial);
//        double height = text.getLayoutBounds().getHeight() * 0.62;
//        // set scaling factor
//        double yScale = 5.0;
//        totalHeight += height * yScale;
//        // apply transformation
//        // TODO determine max width of a single letter (probably W) and x scale to that width
//        getGraphicsContext().scale(1, yScale);
//        // the coordinate system is transformed as well , so you have to transform coordinates as well
//        previousYPosition = previousYPosition + height * yScale;
//        getGraphicsContext().fillText(letter, 100, previousYPosition / yScale);
//        // invert to original scaling
//        getGraphicsContext().scale(1, 1 / yScale);
//        // dor loop ends
//
//        // determine height of letter
//        letter = "C";
//        text = new Text(letter);
//        text.setFont(arial);
//        height = text.getLayoutBounds().getHeight() * 0.62;
//        // set scaling factor
//        yScale = 1.0;
//        totalHeight += height * yScale;
//        // apply transformation
//        getGraphicsContext().scale(1, yScale);
//        // the coordinate system is transformed, so you have to transform coordinates as well
//        previousYPosition = previousYPosition + height * yScale;
//        getGraphicsContext().fillText(letter, 100, previousYPosition / yScale);
//        // invert to original scaling
//        getGraphicsContext().scale(1, 1 / yScale);
//
//        // determine height of letter
//        letter = "W";
//        text = new Text(letter);
//        text.setFont(arial);
//        height = text.getLayoutBounds().getHeight() * 0.62;
//        // set scaling factor
//        yScale = 3.0;
//        totalHeight += height * yScale;
//        // apply transformation
//        getGraphicsContext().scale(1, yScale);
//        // the coordinate system is transformed, so you have to transform coordinates as well
//        previousYPosition = previousYPosition + height * yScale;
//        getGraphicsContext().fillText(letter, 100, previousYPosition / yScale);
//        // invert to original scaling
//        getGraphicsContext().scale(1, 1 / yScale);
//
//        // total height of box
//        strokeStraight(new Vector2D(90, 100), new Vector2D(90, 100+totalHeight));

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
