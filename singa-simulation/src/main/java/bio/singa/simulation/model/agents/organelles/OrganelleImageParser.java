package bio.singa.simulation.model.agents.organelles;

import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.SymmetricMatrix;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentTemplate;
import tec.uom.se.quantity.Quantities;

import javax.imageio.ImageIO;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static LineLikeAgentTemplate getFilaments(Path imageFolder, String baseFileName, NeumannRectangularDirection plusDirection) {

        List<Path> filamentFiles = new ArrayList<>();

        // if directory
        if (Files.isDirectory(imageFolder)) {
            // get all images that match the base name
            try {
                Files.walk(imageFolder)
                        .filter(path -> path.getFileName().toString().startsWith(baseFileName))
                        .forEach(filamentFiles::add);
            } catch (IOException e) {
                throw new UncheckedIOException("Filament image could not be parsed.", e);
            }
        } else {
            throw new IllegalArgumentException("Image folder " + imageFolder + " is no folder.");
        }

        Collections.sort(filamentFiles);

        Quantity<Length> scale = null;
        List<LineLikeAgent> filaments = new ArrayList<>();

        // for each image path
        for (Path imagePath : filamentFiles) {
            // get image
            BufferedImage image;
            try {
                image = ImageIO.read(Files.newInputStream(imagePath));
            } catch (IOException e) {
                throw new UncheckedIOException("Template image " + imagePath + " could not be parsed.", e);
            }

            // create polygon from trace
            OrganelleImageParser parser = new OrganelleImageParser(image);
            parser.createTrace();
            // if there are black scale pixels
            if (!parser.scalePixels.isEmpty() && scale == null) {
                parser.determineScale();
                scale = parser.scale;
            }
            // sort and connect
            List<Vector2D> vectors = Vectors.sortByCloseness(parser.groups.values().iterator().next(), plusDirection);
            filaments.add(new LineLikeAgent(LineLikeAgent.MICROTUBULE, vectors, plusDirection));
        }
        return new LineLikeAgentTemplate(filaments, scale);
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
                        Vector2D vector = new Vector2D(col - 5, row - 5);
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
