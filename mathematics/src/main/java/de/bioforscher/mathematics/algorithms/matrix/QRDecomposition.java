package de.bioforscher.mathematics.algorithms.matrix;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.MatrixUtilities;
import de.bioforscher.mathematics.matrices.RegularMatrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.*;

/**
 * Let A be a real Matrix with dimensions m x n (m > n) with rank(A) = n. A can be decomposed into A = Q * R, where Q
 * with dimensions m x n is orthogonal (Q^T * Q) and R is n x n and a upper triangular Matrix.
 */
public class QRDecomposition {

    /**
     *  The modified Gram-Schmidt Algorithm
     * @param origin
     */
    public static void modifiedGramSchmidtAlgorithm(Matrix origin) {
        // separate matrix into column vectors
        List<Vector> columns = MatrixUtilities.divideIntoColumns(origin);
        // use Gram-Schmidt to calculate orthonormal vectors
        List<Vector> orthonormalizedVectors = VectorUtilities.orthonormalizeVectors(columns);
        // concatenate orthonormal columns
        Matrix q = MatrixUtilities.matrixFromColumns(orthonormalizedVectors);
        System.out.println("Q =\n"+q);
        // compose R from previous results
        Matrix r = composeR(columns, orthonormalizedVectors);
        System.out.println("R =\n"+r);
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
        System.out.println("A =\n"+a);
        modifiedGramSchmidtAlgorithm(a);
    }


}
