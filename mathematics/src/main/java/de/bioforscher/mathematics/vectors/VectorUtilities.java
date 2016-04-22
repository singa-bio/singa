package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.geometry.faces.Rectangle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains only static utility methods to create and handle
 * different Vectors.
 *
 * @author Christoph Leberecht
 * @version 0.0.1
 */
public class VectorUtilities {

    /**
     * prevent instantiation
     */
    private VectorUtilities() {
    }

    /**
     * Generates a random Vector2D that is contained in the given
     * {@link Rectangle}. The random values is inclusive the left and bottom
     * boundary and exclusive the right and top boundary.
     *
     * @param rectangle The rectangle in which the vectors should be included.
     * @return A randomly placed vector.
     */
    public static Vector2D generateRandomVectorInRectangle(Rectangle rectangle) {
        double x = ThreadLocalRandom.current().nextDouble(rectangle.getLeftMostXPosition(),
                rectangle.getRightMostXPosition());
        double y = ThreadLocalRandom.current().nextDouble(rectangle.getBottomMostYPosition(),
                rectangle.getTopMostYPosition());
        return new Vector2D(x, y);
    }

    public static double getMaximalValueForIndex(int index, Vector... vectors) {
        double maximalValue = -Double.MAX_VALUE;
        for (Vector vector : vectors) {
            if (vector.getElement(index) > maximalValue) {
                maximalValue = vector.getElement(index);
            }
        }
        return maximalValue;
    }

    public static double getMinimalValueForIndex(int index, Vector... vectors) {
        double minimalValue = Double.MAX_VALUE;
        for (Vector vector : vectors) {
            if (vector.getElement(index) < minimalValue) {
                minimalValue = vector.getElement(index);
            }
        }
        return minimalValue;
    }

}
