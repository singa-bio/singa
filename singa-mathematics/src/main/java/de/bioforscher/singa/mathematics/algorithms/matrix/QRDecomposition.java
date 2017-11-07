package de.bioforscher.singa.mathematics.algorithms.matrix;

import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.matrices.RegularMatrix;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.List;

/**
 * Let {@code A} be a real matrix with dimensions {@code m x n (m >= n)} and {@code rank(A) = n}. {@code A} can be
 * decomposed into {@code A = Q * R}, where {@code Q} with dimensions {@code m x n} is orthogonal ({@code Q^T * Q = E})
 * and {@code R} is {@code n x n} and a upper triangular matrix. This so called {@code QR} decomposition or {@code QR}
 * factorization is often used to solve the linear least squares problem, and is the basis for a particular eigenvalue
 * algorithm, the QR algorithm.<br>
 * <p>
 * This class implements the modified Gram-Schmidt Algorithm, which uses the {@link
 * Vectors#orthonormalizeVectors(List)} method (see documentation there for more details). <br>
 * <p>
 * Use the static method {@link #calculateQRDecomposition(Matrix)} to obtain a {@link QRDecomposition} that contains the
 * original matrix {@code A}, the orthogonal matrix {@code Q}, and the upper triangular matrix {@code R}.
 */
public class QRDecomposition {

    /**
     * The original Matrix.
     */
    private Matrix originalMatrix;

    /**
     * The orthogonal Matrix.
     */
    private Matrix matrixQ;

    /**
     * The upper triangular matrix.
     */
    private Matrix matrixR;

    /**
     * Initializes the QR decomposition.
     *
     * @param originalMatrix The matrix to be decomposed.
     */
    private QRDecomposition(Matrix originalMatrix) {
        this.originalMatrix = originalMatrix;
    }

    /**
     * Returns the original Matrix, that was the source of the decomposition.
     *
     * @return The original Matrix.
     */
    public Matrix getOriginalMatrix() {
        return originalMatrix;
    }

    /**
     * Returns the orthogonal Matrix Q.
     *
     * @return The orthogonal Matrix Q.
     */
    public Matrix getMatrixQ() {
        return matrixQ;
    }

    /**
     * Returns the upper triangular Matrix R.
     *
     * @return The upper triangular Matrix R.
     */
    public Matrix getMatrixR() {
        return matrixR;
    }

    /**
     * Calculates the QR decomposition of the given matrix, using the modified Graham-Schmidt algorithm.
     *
     * @param originalMatrix The matrix to be decomposed.
     * @return The QR Decomposition
     */
    public static QRDecomposition calculateQRDecomposition(Matrix originalMatrix) {
        // initialize decomposition
        QRDecomposition decomposition = new QRDecomposition(originalMatrix);
        // divide matrix into column vectors
        List<Vector> columns = Matrices.divideIntoColumns(originalMatrix);
        // use Gram-Schmidt process to calculate orthonormal vectors
        List<Vector> orthonormalizedVectors = Vectors.orthonormalizeVectors(columns);
        // assemble orthonormal columns
        decomposition.matrixQ = Matrices.assembleMatrixFromColumns(orthonormalizedVectors);
        // compose R from previous results
        decomposition.matrixR = composeR(columns, orthonormalizedVectors);
        return decomposition;
    }

    /**
     * Composes the upper triangular Matrix R from the original column vectors and the orthonormalized Vectors.
     *
     * @param originalColumns        The original column vectors.
     * @param orthonormalizedVectors The orthonormalized vectors.
     * @return The upper triangular Matrix R.
     */
    private static Matrix composeR(List<Vector> originalColumns, List<Vector> orthonormalizedVectors) {
        double[][] elements = new double[originalColumns.size()][originalColumns.size()];
        for (int column = 0; column < originalColumns.size(); column++) {
            // only for the upper triangle of the matrix
            for (int row = column; row < originalColumns.size(); row++) {
                // each element is the dot product from the orthonormalized vector and the original column
                elements[column][row] = orthonormalizedVectors.get(column).dotProduct(originalColumns.get(row));
            }
        }
        return new RegularMatrix(elements);
    }

}
