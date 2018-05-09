package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.core.utility.GifWriter;
import de.bioforscher.singa.features.model.QuantityFormatter;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.renderer.AutomatonGraphRenderer;
import de.bioforscher.singa.simulation.renderer.RenderingMode;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.measure.quantity.Time;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.SECOND;

public class GraphImageWriter implements UpdateEventListener<GraphUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(GraphImageWriter.class);
    private static final QuantityFormatter<Time> TIME_FORMATTER = new QuantityFormatter<>(MICRO(SECOND), true);

    /**
     * The path to the user defined workspace.
     */
    private final Path workspacePath;

    /**
     * The folder for the current simulation.
     */
    private final Path folder;

    /**
     * The entities that should be observed.
     */
    private final List<ChemicalEntity> observedEntities;

    private Map<ChemicalEntity, Path> paths;

    private static final String CHART_FILE_PREFIX = "graph_";

    private static final double DEFAULT_DRAWING_WIDTH = 500.0;
    private static final double DEFAULT_DRAWING_HEIGHT = 500.0;
    private static final boolean DEFAULT_JFX_SHUTDOWN = true;

    private ExecutorService rendererExecutor;
    private ExecutorService exportExecutor;

    private double drawingWidth = DEFAULT_DRAWING_WIDTH;
    private double drawingHeight = DEFAULT_DRAWING_HEIGHT;

    private boolean jfxShutdown = DEFAULT_JFX_SHUTDOWN;

    public GraphImageWriter(Path workspacePath, Path folder, List<ChemicalEntity> observedEntities) {
        this.workspacePath = workspacePath;
        this.folder = folder;
        this.observedEntities = observedEntities;
        paths = new HashMap<>();
        createFolderStructure();
        rendererExecutor = createExecutor();
        exportExecutor = createExecutor();
    }

    public void setJfxShutdown(boolean jfxShutdown) {
        this.jfxShutdown = jfxShutdown;
    }

    private static int compareFileNames(Path first, Path second) {
        return extractTimeStamp(first).compareTo(extractTimeStamp(second));
    }

    private static String extractTimeStamp(Path path) {
        String name = path.getFileName().toString();
        return name.substring(name.indexOf("_"), name.indexOf("."));
    }

    /**
     * Initialized the folder for the current simulation.
     */
    private void createFolderStructure() {
        Path workspaceFolder = workspacePath.resolve(folder);
        try {
            if (!Files.exists(workspaceFolder)) {
                Files.createDirectory(workspaceFolder);
            }
            for (ChemicalEntity observedEntity : observedEntities) {
                Path path = workspaceFolder.resolve(observedEntity.getIdentifier().getIdentifier().replace("(\\W|^_)*", "_"));
                paths.put(observedEntity, path);
                if (Files.exists(path)) {
                    deleteDirectory(path.toFile());
                }
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        directoryToBeDeleted.delete();
    }

    public double getDrawingWidth() {
        return drawingWidth;
    }

    public void setDrawingWidth(double drawingWidth) {
        this.drawingWidth = drawingWidth;
    }

    public double getDrawingHeight() {
        return drawingHeight;
    }

    public void setDrawingHeight(double drawingHeight) {
        this.drawingHeight = drawingHeight;
    }

    public void shutDown() {
        rendererExecutor.shutdown();
        exportExecutor.shutdown();
        if (jfxShutdown) {
            Platform.exit();
        }
    }

    public void combineToGif() {
        logger.debug("Rendering gif simulation progression.");
        for (Path entityPath : paths.values()) {
            try {
                GifWriter writer = new GifWriter(entityPath.resolve("sequence.gif"));
                // write GIF
                Files.walk(entityPath)
                        .filter(Files::isRegularFile)
                        .filter(file -> file.getFileName().toString().endsWith("png"))
                        .sorted(GraphImageWriter::compareFileNames)
                        .forEach(path -> appendImage(writer, path));
                // end gif and close streams
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendImage(GifWriter writer, Path path) {
        try {
            BufferedImage nextImage = ImageIO.read(path.toFile());
            writer.writeToSequence(nextImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        // for each entity that is observed
        for (ChemicalEntity observedEntity : observedEntities) {
            // latch is required since the updates have to wait till the current state of the simulation is drawn
            CountDownLatch latch = new CountDownLatch(1);
            // new task for the current graph
            RenderGraphsTask task = new RenderGraphsTask(latch, event, observedEntity);
            // execute the task
            rendererExecutor.execute(task);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ExecutorService createExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    class RenderGraphsTask extends Task<Canvas> {

        private final GraphUpdatedEvent event;
        private final AutomatonGraphRenderer renderer;

        RenderGraphsTask(CountDownLatch callbackLatch, GraphUpdatedEvent event, ChemicalEntity observedEntity) {
            // initialize rendering
            this.event = event;
            renderer = new AutomatonGraphRenderer();
            renderer.getBioRenderingOptions().setRenderingMode(RenderingMode.ENTITY_BASED);
            renderer.getBioRenderingOptions().setNodeHighlightEntity(observedEntity);
            renderer.drawingWidthProperty().setValue(drawingWidth);
            renderer.drawingHeightProperty().setValue(drawingHeight);
            Function<AutomatonGraph, Void> renderTimeStamp = timeStepRendering -> {
                renderer.getGraphicsContext().setFill(Color.BLACK);
                renderer.drawTextCenteredOnPoint(TIME_FORMATTER.format(event.getElapsedTime()), new Vector2D(renderer.getDrawingWidth() * 0.6, renderer.getDrawingHeight() * 0.85));
                return null;
            };
            renderer.setRenderAfter(renderTimeStamp);
            renderer.renderVoronoi(false);
            // attach task to snapshot rendering
            setOnSucceeded(handler -> {
                try {
                    CreateSnapshotsTask task1 = new CreateSnapshotsTask(callbackLatch, get(), observedEntity);
                    exportExecutor.execute(task1);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected Canvas call() {
            Canvas canvas = new Canvas(drawingWidth, drawingHeight);
            Platform.runLater(() -> {
                logger.trace("Rendering current graph.");
                canvas.getGraphicsContext2D();
                renderer.setGraphicsContext(canvas.getGraphicsContext2D());
                renderer.render(event.getGraph());
            });
            return canvas;
        }

    }

    class CreateSnapshotsTask extends Task<BufferedImage> {

        private final Canvas canvas;

        CreateSnapshotsTask(CountDownLatch callbackLatch, Canvas canvas, ChemicalEntity observedEntity) {
            this.canvas = canvas;
            // attach task to export snapshot
            setOnSucceeded(handler -> {
                try {
                    ExportImagesTask task = new ExportImagesTask(callbackLatch, get(), observedEntity);
                    exportExecutor.execute(task);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected BufferedImage call() throws Exception {
            logger.trace("Taking snapshot of rendered graph.");
            return snapshotChart(canvas);
        }

        private BufferedImage snapshotChart(final Canvas canvas) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            final SimpleObjectProperty<BufferedImage> imageProperty = new SimpleObjectProperty<>();
            Platform.runLater(() -> {
                final SnapshotParameters snapshotParameters = new SnapshotParameters();
                canvas.snapshot(result -> {
                            imageProperty.set(SwingFXUtils.fromFXImage(result.getImage(), null));
                            latch.countDown();
                            return null;
                        },
                        snapshotParameters, null
                );
            });
            latch.await();
            return imageProperty.get();
        }
    }

    class ExportImagesTask extends Task<Void> {

        private final ChemicalEntity chemicalEntity;
        private CountDownLatch callbackLatch;
        private final BufferedImage image;

        ExportImagesTask(CountDownLatch callbackLatch, BufferedImage image, ChemicalEntity chemicalEntity) {
            this.chemicalEntity = chemicalEntity;
            this.callbackLatch = callbackLatch;
            this.image = image;
        }

        @Override
        protected Void call() {
            Path path = generatePath(chemicalEntity);
            logger.trace("Exporting graph to image {}.", path);
            exportPng(image, path);
            callbackLatch.countDown();
            return null;
        }

        private void exportPng(BufferedImage image, Path path) {
            try {
                ImageIO.write(image, "png", path.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not save image " + path, e);
            }
        }

        private Path generatePath(ChemicalEntity chemicalEntity) {
            return paths.get(chemicalEntity).resolve(CHART_FILE_PREFIX + System.currentTimeMillis() + ".png");
        }

    }

}