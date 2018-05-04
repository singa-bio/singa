package de.bioforscher.singa.simulation.renderer;

import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.simulation.events.GraphUpdatedEvent;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class OffScreenOffThreadCharts implements UpdateEventListener<GraphUpdatedEvent> {

    private static final String CHART_FILE_PREFIX = "chart_";
    private static final Path WORKING_DIR = Paths.get("/tmp/charts/");

    private final AutomatonGraphRenderer renderer;

    private ExecutorService renderGraphsExecutor = createExecutor("Render");
    private ExecutorService chartsSnapshotExecutor = createExecutor("Snapshot");

    public OffScreenOffThreadCharts() {
        renderer = new AutomatonGraphRenderer(null);
        renderer.drawingWidthProperty().setValue(500.0);
        renderer.drawingHeightProperty().setValue(500.0);
    }

    public void shutDown() {
        renderGraphsExecutor.shutdown();
        chartsSnapshotExecutor.shutdown();
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        CountDownLatch callbackLatch = new CountDownLatch(1);
        RenderGraphsTask task = new RenderGraphsTask(callbackLatch, event.getGraph());
        renderGraphsExecutor.execute(task);
        try {
            callbackLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ExecutorService createExecutor(final String name) {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setName(name);
            t.setDaemon(true);
            return t;
        };
        return Executors.newSingleThreadExecutor(factory);
    }

    class RenderGraphsTask extends Task<Canvas> {

        private AutomatonGraph graph;

        RenderGraphsTask(CountDownLatch callbackLatch, AutomatonGraph graph) {
            this.graph = graph;
            setOnSucceeded(handler -> {
                try {
                    CreateSnapshotsTask task1 = new CreateSnapshotsTask(callbackLatch, get());
                    chartsSnapshotExecutor.execute(task1);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected Canvas call() {
            Canvas canvas = new Canvas(500, 500);
            Platform.runLater(() -> {
                System.out.println("rendering");
                canvas.getGraphicsContext2D();
                renderer.setGraphicsContext(canvas.getGraphicsContext2D());
                renderer.render(graph);
            });
            return canvas;
        }

    }

    class CreateSnapshotsTask extends Task<BufferedImage> {

        private final Canvas canvas;

        public CreateSnapshotsTask(CountDownLatch callbackLatch, Canvas canvas) {
            this.canvas = canvas;
            setOnSucceeded(handler -> {
                try {
                    ExportImagesTask task = new ExportImagesTask(callbackLatch, get());
                    chartsSnapshotExecutor.execute(task);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected BufferedImage call() throws Exception {
            System.out.println("taking snapshot");
            return snapshotChart(canvas);
        }

        private BufferedImage snapshotChart(final Canvas canvas) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            // render the chart in an off screen scene (scene is used to allow css processing) and snapshot it to an image.
            // the snapshot is done in run later as it must occur on the javafx application thread.
            final SimpleObjectProperty<BufferedImage> imageProperty = new SimpleObjectProperty<>();
            Platform.runLater(() -> {
                final SnapshotParameters params = new SnapshotParameters();
                canvas.snapshot(
                        result -> {
                            imageProperty.set(SwingFXUtils.fromFXImage(result.getImage(), null));
                            latch.countDown();
                            return null;
                        },
                        params,
                        null
                );
            });
            latch.await();
            return imageProperty.get();
        }
    }

    class ExportImagesTask extends Task<Void> {

        private CountDownLatch callbackLatch;
        private final BufferedImage image;

        public ExportImagesTask(CountDownLatch callbackLatch, BufferedImage image) {
            this.callbackLatch = callbackLatch;
            this.image = image;
        }

        @Override
        protected Void call() throws Exception {
            System.out.println("exporting Image");
            exportPng(image, generatePath());
            callbackLatch.countDown();
            return null;
        }

        private void exportPng(BufferedImage image, Path path) {
            try {
                ImageIO.write(image, "png", path.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not save image to file + filename", e);
            }
        }

        private Path generatePath() {
            return WORKING_DIR.resolve(CHART_FILE_PREFIX + System.currentTimeMillis() + ".png");
        }

    }

}