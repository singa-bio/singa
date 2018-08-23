package bio.singa.simulation.parser.organelles;

import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.SymmetricMatrix;
import bio.singa.mathematics.vectors.Vector2D;
import tec.uom.se.quantity.Quantities;

import javax.imageio.ImageIO;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class OrganelleImageParser {

    private static final Quantity<Length> DEFAULT_SCALE_LENGTH = Quantities.getQuantity(100.0, NANO(METRE));

    private BufferedImage templateImage;
    private List<Vector2D> scalePixels;
    private List<Vector2D> templatePixels;

    private Map<Integer, Set<Vector2D>> groups;

    private Polygon polygon;
    private Quantity<Length> scale;

    public static OrganelleTemplate getOrganelleTemplate(String resourceLocation) {
        // get image
        // String resourceLocation = "organelle_templates/" + organelles.name().toLowerCase() + ".png";
        InputStream resource = Resources.getResourceAsStream(resourceLocation);
        BufferedImage image;
        try {
            image = ImageIO.read(resource);
        } catch (IOException e) {
            throw new UncheckedIOException("Template image " + resourceLocation + " could not be parsed.", e);
        }
        // create polygon from trace
        OrganelleImageParser parser = new OrganelleImageParser(image);
        parser.createTrace();
        parser.determineScale();
        // return polygon and scale
        return new OrganelleTemplate(parser.scale, parser.polygon, parser.groups);
    }

    public OrganelleImageParser(BufferedImage templateImage) {
        this.templateImage = templateImage;
        templatePixels = new ArrayList<>();
        scalePixels = new ArrayList<>();
        groups = new HashMap<>();
    }

    private void createTrace() {
        List<Vector2D> vectors = convertToVectors(templateImage);
        // relies on the angular sorting of the vectors
        polygon = new VertexPolygon(vectors);
    }

    private List<Vector2D> convertToVectors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        // for each pixel
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // get rgb values
                int rgb = image.getRGB(col, row);
//                int red = (rgb >> 16) & 0x000000FF;
//                int green = (rgb >> 8) & 0x000000FF;
//                int blue = (rgb) & 0x000000FF;
                // decide based on color
                if (isNotWhite(rgb)) {
                    // white pixels are ignored
                    if (isBlack(rgb)) {
                        // black pixels are scale
                        scalePixels.add(new Vector2D(col, row));
                    } else {
                        // colored pixels will be traced and assigned to groups
                        Vector2D vector = new Vector2D(col, row);
                        templatePixels.add(vector);
                        addToGroup(rgb, vector);
                    }
                }
            }
        }
        return templatePixels;
    }

    private void addToGroup(int rgbValue, Vector2D vector) {
        if (!groups.containsKey(rgbValue)) {
            groups.put(rgbValue, new HashSet<>());
        }
        groups.get(rgbValue).add(vector);
    }

    private static boolean isNotWhite(int rgbValue) {
        return rgbValue != -1;
    }

    private static boolean isBlack(int rgbValue) {
        return rgbValue == -16777216;
    }

    private List<LineSegment> connect(List<Vector2D> vectors) {
        final double errorCutoff = templateImage.getWidth() * 0.3;
        final Vector2D first = vectors.iterator().next();
        List<LineSegment> segments = new ArrayList<>();
        List<Vector2D> copy = new ArrayList<>(vectors);
        copy.remove(first);
        Vector2D previous = first;
        // for each vector
        while (!copy.isEmpty()) {
            // determine closest neighbour
            Map.Entry<Vector2D, Double> entry = EUCLIDEAN_METRIC.calculateClosestDistance(copy, previous);
            if (entry.getValue() > errorCutoff) {
                // remove nonsense connections
                copy.remove(entry.getKey());
            } else {
                // add line segment
                Vector2D next = entry.getKey();
                segments.add(new SimpleLineSegment(previous, next));
                copy.remove(next);
                previous = next;
            }
        }
        segments.add(new SimpleLineSegment(first, previous));
        return segments;
    }

    private void determineScale() {
        // get from scale pixels
        SymmetricMatrix symmetricMatrix = EUCLIDEAN_METRIC.calculateDistancesPairwise(scalePixels);
        // get maximal extend (length)
        List<Pair<Integer>> positionsOfMaximalElement = Matrices.getPositionsOfMaximalElement(symmetricMatrix);
        double length = symmetricMatrix.getElement(positionsOfMaximalElement.get(0).getFirst(), positionsOfMaximalElement.get(0).getSecond());
        // calculate dimensionality of one pixel (e.g. 100 nm : 10 px -> 10 nm per px)
        scale = DEFAULT_SCALE_LENGTH.divide(length);
    }

}
