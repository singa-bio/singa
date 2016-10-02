package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.concepts.Addable;
import de.bioforscher.mathematics.geometry.faces.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains only static utility methods to create and handle different Vectors.
 *
 * @author cl
 */
public class VectorUtilities {

    /**
     * prevent instantiation
     */
    private VectorUtilities() {
    }

    /**
     * Generates a random Vector2D that is contained in the given {@link Rectangle}. The random values is inclusive the
     * left and bottom boundary and exclusive the right and top boundary.
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

    /**
     * Compares all values of the given index for all given vectors and returns the largest value found.
     *
     * @param index The index of the value, that is to be compared.
     * @param vectors A collection of Vectors.
     * @return The maximal value of the given index.
     */
    public static double getMaximalValueForIndex(int index, Vector... vectors) {
        double maximalValue = -Double.MAX_VALUE;
        for (Vector vector : vectors) {
            if (vector.getElement(index) > maximalValue) {
                maximalValue = vector.getElement(index);
            }
        }
        return maximalValue;
    }

    /**
     * Compares all values of the given index for all given vectors and returns the smallest value found.
     *
     * @param index The index of the value, that is to be compared.
     * @param vectors A collection of Vectors.
     * @return The minimal value of the given index.
     */
    public static double getMinimalValueForIndex(int index, Vector... vectors) {
        double minimalValue = Double.MAX_VALUE;
        for (Vector vector : vectors) {
            if (vector.getElement(index) < minimalValue) {
                minimalValue = vector.getElement(index);
            }
        }
        return minimalValue;
    }

    /**
     * Computes the centroid of all vectors in the collection by summing them and dividing by the number of vectors in
     * the collection.
     *
     * @param vectors The vectors to calculate the centroid from.
     * @return The centroid.
     */
    public static Vector getCentroid(Collection<Vector> vectors) {
        return Addable.sum(vectors).divide(vectors.size());
    }

    /**
     * This method creates an orthonormalized set of vectors in an inner product space, in this case the Euclidean space
     * R^n equipped with the standard inner product ({@link Vector#dotProduct(Vector)}). The Gram-Schmidt process takes
     * a finite, linearly independent list S = {v1, ..., vk} for k ≤ n and generates an orthogonal list S′ = {u1, ...,
     * uk} that spans the same k-dimensional subspace of R^n as S.
     *
     * @param vectors The original vectors to be orthonormalized.
     * @return A list of orthonormal vectors.
     * @see <a href="https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process">Wikipedia: Gram-Schmidt process</a>
     */
    public static List<Vector> orthonormalizeVectors(List<Vector> vectors) {
        // using modified Gram-Schmidt process

        // all vectors need to have the same dimensionality
        if (!Vector.haveSameDimensions(vectors)) {
            throw new IllegalArgumentException("All vectors need to have the same dimensionality.");
        }
        int dimension = vectors.iterator().next().getDimension();
        // the number of vectors needs to be equal or smaller than the dimension of the vectors
        if (vectors.size() > dimension) {
            throw new IllegalArgumentException("The number of vectors needs to be equal or smaller than the dimension" +
                    " of the vectors");
        }
        // orthonormalize the given vectors
        List<Vector> orthonormalizedVectors = new ArrayList<>();
        List<Vector> normalizedVectors = new ArrayList<>();
        for (Vector vector : vectors) {
            if (orthonormalizedVectors.isEmpty()) {
                // just normalize
                orthonormalizedVectors.add(vector);
                normalizedVectors.add(vector.normalize());
            } else {
                // successively modify the original vector with the existing orthonormalized vectors
                Vector projectionSum = accumulateGramSchmidtProjection(vector, orthonormalizedVectors);
                // lastly, normalize
                orthonormalizedVectors.add(projectionSum);
                normalizedVectors.add(projectionSum.normalize());
            }
        }
        return normalizedVectors;
    }

    /**
     * The projection for the Graham-Schmidt process. This operator projects the first vector (v) orthogonally onto the
     * line spanned by the second vector (u). If u is the zero vector the projection will be also the zero vector. The
     * projection is calculated by:
     * <p>
     * proj(u,v) = ((v . u) / (u . u)) * u
     * <p>
     * with ( . ) denoting the inner product (generally the dot product)
     *
     * @param first The vector (v) to be projected.
     * @param second The vector (u) to project with.
     * @return The projected vector.
     */
    public static Vector gramSchmidtProjection(Vector first, Vector second) {
        if (!second.isZero()) {
            return second.multiply(first.dotProduct(second) / second.dotProduct(second));
        } else {
            return second;
        }
    }

    public static Vector accumulateGramSchmidtProjection(Vector vector, List<Vector> orthogonalizedVectors) {
        // successively modify the original vector with the existing orthonormalized vectors
        Vector projectionSum = new RegularVector(vector.getDimension());
        boolean firstRun = true;
        for (Vector orthonormalizedVector : orthogonalizedVectors) {
            if (firstRun) {
                // in the first run use th original vector
                projectionSum = vector.subtract(gramSchmidtProjection(vector, orthonormalizedVector));
                firstRun = false;
            } else {
                // than accumulate changes
                projectionSum = projectionSum.subtract(gramSchmidtProjection(projectionSum, orthonormalizedVector));
            }
        }
        return projectionSum;
    }


}
