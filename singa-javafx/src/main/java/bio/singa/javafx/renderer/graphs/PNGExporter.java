package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.geometry.faces.Rectangle;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import sun.awt.image.IntegerComponentRaster;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

/**
 * @author cl
 */
public class PNGExporter {

    private PNGExporter() {
    }

    public static void exportGraphToPNG(File file, GraphRenderer renderer, Canvas canvas) {
        if (file == null) {
            return;
        }

        Rectangle boundingBox = renderer.getBoundingBox();
        int drawingWidth = (int) boundingBox.getWidth();
        int drawingHeight = (int) boundingBox.getHeight();
        SnapshotParameters sp = new SnapshotParameters();
        // bonds for menu offset
        Bounds boundsInScene = canvas.localToScene(canvas.getBoundsInLocal());
        double menuOffset = boundsInScene.getMinY();
        // snapshot only graph
        sp.setViewport(new Rectangle2D(boundingBox.getLeftMostXPosition(), boundingBox.getTopMostYPosition()+menuOffset, drawingWidth, drawingHeight));
        sp.setFill(Color.TRANSPARENT);
        WritableImage img = new WritableImage(drawingWidth, drawingHeight);
        canvas.snapshot(sp, img);

        BufferedImage bufferedImage = new BufferedImage(drawingWidth, drawingHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        IntegerComponentRaster raster = (IntegerComponentRaster) bufferedImage.getRaster();
        int offset = raster.getDataOffset(0);
        int scan = raster.getScanlineStride();
        int[] data = raster.getDataStorage();
        WritablePixelFormat<IntBuffer> pf = PixelFormat.getIntArgbPreInstance();
        img.getPixelReader().getPixels(0, 0, drawingWidth, drawingHeight, pf, data, offset, scan);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
