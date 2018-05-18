package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tec.uom.se.quantity.Quantities;

import java.util.concurrent.ThreadLocalRandom;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class VesiclePlayground extends Application implements Renderer {

    private Canvas canvas;
    private VesicleLayer layer;
    private AnimationTimer timer;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(500, 500);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Rectangle rectangle = new Rectangle(500, 500);
        layer = new VesicleLayer(rectangle);

        // define scales
        // todo should be initialized automatically
        EnvironmentalParameters.setSystemLength(Quantities.getQuantity(20, MICRO(METRE)));
        EnvironmentalParameters.setSimulationLength(500);
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));
        System.out.println(EnvironmentalParameters.convertSystemToSimulationScale(Quantities.getQuantity(1, MICRO(METRE))));
        System.out.println(EnvironmentalParameters.convertSimulationToSystemScale(1));

        // instantiate vesicles
        for (int i = 0; i < 50; i++) {
            Vesicle vesicle = new Vesicle(Vectors.generateRandom2DVector(rectangle), Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE)));
            layer.addVesicle(vesicle);
        }

        renderVesicles();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveVesicles();
            }
        };
        timer.start();

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void moveVesicles() {
        layer.nextEpoch();
        renderVesicles();
    }

    public void renderVesicles() {
        // background
        getGraphicsContext().setFill(Color.WHITE);
        getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        // vesicles
        getGraphicsContext().setFill(Color.BLACK);
        for (Vesicle vesicle : layer.getVesicles()) {
            circlePoint(vesicle.getPosition(), EnvironmentalParameters.convertSystemToSimulationScale(vesicle.getRadius()));
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
