package bio.singa.simulation.parser.organelles;

import bio.singa.core.utility.Resources;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.LineSegmentPolygon;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vector2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class OrganelleImageParser {

    public enum OrganelleType {
        EARLY_ENDOSOME
    }

    public static LineSegmentPolygon getPolygonTemplate(OrganelleType organelles) {
        InputStream resource = Resources.getResourceAsStream("organelle_templates/" + organelles.name().toLowerCase() + ".png");
        try {
            BufferedImage image = ImageIO.read(resource);
            List<Vector2D> vectors = convertToVectors(image);
            List<LineSegment> segments = connect(vectors);
            return new LineSegmentPolygon(segments);
        } catch (IOException e) {
            throw new UncheckedIOException("Resource could not be parsed", e);
        }
    }

    private static List<Vector2D> convertToVectors(BufferedImage image) {
        List<Vector2D> vectors = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = image.getRGB(col, row);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                if (red == 255 && green == 0 && blue == 0) {
                    vectors.add(new Vector2D(row, col));
                }
            }
        }
        return vectors;
    }

    private static List<LineSegment> connect(List<Vector2D> vectors) {
        List<LineSegment> segments = new ArrayList<>();
        final Vector2D first = vectors.iterator().next();
        List<Vector2D> copy = new ArrayList<>(vectors);
        copy.remove(first);
        Vector2D previous = first;
        while (!copy.isEmpty()) {
            Map.Entry<Vector2D, Double> entry = VectorMetricProvider.EUCLIDEAN_METRIC.calculateClosestDistance(copy, previous);
            Vector2D next = entry.getKey();
            segments.add(new SimpleLineSegment(previous, next));
            copy.remove(next);
            previous = next;
        }
        segments.add(new SimpleLineSegment(first, previous));
        return segments;
    }

}
