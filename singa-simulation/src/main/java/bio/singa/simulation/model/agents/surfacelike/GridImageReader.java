package bio.singa.simulation.model.agents.surfacelike;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class GridImageReader {

    private BufferedImage image;
    private int[][] grid;
    private Set<Integer> uniqueColors;

    public GridImageReader(BufferedImage image) {
        uniqueColors = new HashSet<>();
        this.image = image;
    }

    public static GridImageReader readTemplate(String resourceLocation) {
        // get image
        BufferedImage image;
        try {
            image = ImageIO.read(Paths.get(resourceLocation).toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Template image " + resourceLocation + " could not be parsed.", e);
        }
        GridImageReader reader = new GridImageReader(image);
        reader.convert();
        return reader;
    }

    private void convert() {
        int width = image.getWidth();
        int height = image.getHeight();
        // for each pixel
        grid = new int[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // get rgb values
                int color = image.getRGB(col, row);
                grid[col][row] = color;
                uniqueColors.add(color);
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public Set<Integer> getUniqueColors() {
        return uniqueColors;
    }

}
