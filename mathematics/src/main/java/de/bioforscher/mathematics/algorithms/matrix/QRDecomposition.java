package de.bioforscher.mathematics.algorithms.matrix;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.MatrixUtilities;
import de.bioforscher.mathematics.matrices.RegularMatrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.*;

/**
 * Let {@code A} be a real matrix with dimensions {@code m x n (m >= n)} with {@code rank(A) = n}. {@code A} can be
 * decomposed into {@code A = Q * R}, where {@code Q} with dimensions {@code m x n} is orthogonal ({@code Q^T * Q = E})
 * and {@code R} is {@code n x n} and a upper triangular matrix. This so called {@code QR} decomposition or {@code QR}
 * factorization is often used to solve the linear least squares problem, and is the basis for a particular eigenvalue
 * algorithm, the QR algorithm.</br>
 * <p>
 * This class implements the modified Gram-Schmidt Algorithm, which uses the {@link
 * VectorUtilities#orthonormalizeVectors(List)} method (see documentation there for more details). </br>
 * <p>
 * Use the static method {@link #calculateQRDecomposition(Matrix)} to obtain a {@link QRDecomposition} that contains the
 * original matrix {@code A}, the orthogonal matrix {@code Q}, and the upper triangular matrix {@code R}.
 */
public class QRDecomposition {

    private Matrix originalMatrix;
    private Matrix matrixQ;
    private Matrix matrixR;

    private QRDecomposition(Matrix originalMatrix) {
        this.originalMatrix = originalMatrix;
    }

    public Matrix getOriginalMatrix() {
        return this.originalMatrix;
    }

    public Matrix getMatrixQ() {
        return this.matrixQ;
    }

    public Matrix getMatrixR() {
        return this.matrixR;
    }

    /**
     * The modified Gram-Schmidt Algorithm is used to calculate the QR decomposition of the given matrix.
     *
     * @param originalMatrix
     */
    public static QRDecomposition calculateQRDecomposition(Matrix originalMatrix) {
        QRDecomposition decomposition = new QRDecomposition(originalMatrix);
        // separate matrix into column vectors
        List<Vector> columns = MatrixUtilities.divideIntoColumns(originalMatrix);
        // use Gram-Schmidt to calculate orthonormal vectors
        List<Vector> orthonormalizedVectors = VectorUtilities.orthonormalizeVectors(columns);
        // concatenate orthonormal columns
        decomposition.matrixQ = MatrixUtilities.matrixFromColumns(orthonormalizedVectors);
        // compose R from previous results
        decomposition.matrixR = composeR(columns, orthonormalizedVectors);
        return decomposition;
    }

    private static Matrix composeR(List<Vector> originalColumns, List<Vector> orthonormalizedVectors) {
        double[][] elements = new double[originalColumns.size()][originalColumns.size()];
        for (int column = 0; column < originalColumns.size(); column++) {
            for (int row = column; row < originalColumns.size(); row++) {
                elements[column][row] = orthonormalizedVectors.get(column).dotProduct(originalColumns.get(row));
            }
        }
        return new RegularMatrix(elements);
    }

    public static void main(String[] args) {
        System.out.println("A = QR");
        System.out.println();
        Matrix a = new RegularMatrix(new double[][]{{12, -51, 4}, {6, 167, -68}, {-4, 24, -41}});
        System.out.println("A =\n" + a);
        calculateQRDecomposition(a);
    }


}
