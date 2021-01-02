package bio.singa.mathematics.vectors;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class Vectors2D {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Vectors2D.class);

    /**
     * Calculates the the angle between two vectors, where the sign of the angle indicates a clockwise (-) or
     * counter-clockwise (+) rotation. The angle is always between -pi and pi.
     *
     * @param frist The first vector.
     * @param second The second vector.
     * @return The directional angle between them.
     */
    public static double getDirectionalAngle(Vector2D frist, Vector2D second) {
        return Math.atan2(frist.getX() * second.getY() - frist.getY() * second.getX(), frist.getX() * second.getX() + frist.getY() * second.getY());
    }


    public static List<Vector2D> generateMultipleRandom2DVectors(int numberOfVectors, Rectangle rectangle) {
        List<Vector2D> vectors = new ArrayList<>(numberOfVectors);
        for (int i = 0; i < numberOfVectors; i++) {
            vectors.add(generateRandom2DVector(rectangle));
        }
        return vectors;
    }

    public static List<Vector2D> generateMultipleRandom2DVectors(int numberOfVectors, Polygon polygon) {
        Rectangle boundingBox = Polygons.getMinimalBoundingBox(polygon);
        List<Vector2D> vectors = new ArrayList<>(numberOfVectors);
        int maximalTries = numberOfVectors * 100;
        int generatedVectors = 0;
        while (vectors.size() < numberOfVectors && generatedVectors < maximalTries) {
            Vector2D vector = generateRandom2DVector(boundingBox);
            if (polygon.containsVector(vector)) {
                vectors.add(vector);
            }
            generatedVectors++;
        }
        if (maximalTries <= generatedVectors) {
            logger.error("Maximal number of tries exceeded while generating random vectors in a polygon. Returned empty list.");
            return Collections.emptyList();
        }
        return vectors;
    }


    /**
     * Generates a random Vector2D that is contained in the given {@link Rectangle}. The random values is inclusive the
     * left and bottom boundary and exclusive the right and top boundary.
     *
     * @param rectangle The rectangle in which the vectors should be included.
     * @return A randomly placed vector.
     */
    public static Vector2D generateRandom2DVector(Rectangle rectangle) {
        double x = ThreadLocalRandom.current().nextDouble(rectangle.getLeftMostXPosition(),
                rectangle.getRightMostXPosition());
        double y = ThreadLocalRandom.current().nextDouble(rectangle.getTopMostYPosition(),
                rectangle.getBottomMostYPosition());
        return new Vector2D(x, y);
    }

    public static Vector2D generateStandardGaussian2DVector() {
        double x = ThreadLocalRandom.current().nextGaussian();
        double y = ThreadLocalRandom.current().nextGaussian();
        return new Vector2D(x, y);
    }

    public static Vector2D generateRandomUnit2DVector() {
        double x = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        double y = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        return new Vector2D(x, y).normalize();
    }

    public static List<Vector2D> sortByCloseness(Collection<Vector2D> vectors) {
        final Vector2D first = vectors.iterator().next();
        List<Vector2D> copy = new ArrayList<>(vectors);
        List<Vector2D> result = new ArrayList<>();
        result.add(first);
        copy.remove(first);
        Vector2D previous = first;
        // for each vector (and omit last connection)
        while (copy.size() > 0) {
            // determine closest neighbour
            Map.Entry<Vector2D, Double> entry = EUCLIDEAN_METRIC.calculateClosestDistance(copy, previous);
            // add line segment
            Vector2D next = entry.getKey();
            result.add(next);
            copy.remove(next);
            previous = next;
        }
        return result;
    }

    public static List<LineSegment> connectToSegments(List<Vector2D> vectors) {
        Iterator<Vector2D> iterator = vectors.iterator();
        Vector2D previous = iterator.next();
        List<LineSegment> lineSegments = new ArrayList<>();
        while (iterator.hasNext()) {
            Vector2D next = iterator.next();
            lineSegments.add(new SimpleLineSegment(previous, next));
            previous = next;
        }
        return lineSegments;
    }

    /**
     * Computes the centroid of all vectors in the collection by summing them and dividing by the number of vectors in
     * the collection. This is faster than using the general implementation from the {@link Vectors} class.
     *
     * @param vectors The vectors to calculate the centroid from.
     * @return The centroid.
     */
    public static Vector2D get2DCentroid(Collection<Vector2D> vectors) {
        int vectorCount = vectors.size();
        double[] sum = new double[3];
        for (Vector2D vector : vectors) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
        }
        return new Vector2D(sum[0] / vectorCount, sum[1] / vectorCount);
    }

}
