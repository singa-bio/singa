package bio.singa.mathematics.matrices;

import bio.singa.mathematics.vectors.Vector;

import java.util.List;

/**
 * Fast(ish) Matrix operations you can use if you are sure what you are doing.
 *
 * @author cl
 */
public class FastMatrices {

    /**
     * Creates a regular matrix without checking if the values are well formed or symmetric.
     *
     * @param values Values the matrix should have.
     * @return A regular matrix.
     */
    public static RegularMatrix createRegularMatrix(double[][] values) {
        return new RegularMatrix(values, values.length, values[0].length);
    }

    /**
     * Creates a symmetric matrix without checking if the array has the right format or has to be compacted. The method
     * requires a jagged lower triangle array like:
     * <pre>
     * {{1.0}, {2.0, 5.0}, {3.0, 6.0, 9.0}} </pre>
     *
     * @param values Jagged lower triangle array with the values.
     * @return A symmetric matrix.
     */
    public static SquareMatrix createSquareMatrix(double[][] values) {
        return new SquareMatrix(values, values.length, values[values.length - 1].length);
    }

    /**
     * Creates a symmetric matrix without checking if the array has the right format or has to be compacted. The method
     * requires a jagged lower triangle array like:
     * <pre>
     * {{1.0}, {2.0, 5.0}, {3.0, 6.0, 9.0}} </pre>
     *
     * @param values Jagged lower triangle array with the values.
     * @return A symmetric matrix.
     */
    public static SymmetricMatrix createSymmetricMatrix(double[][] values) {
        return new SymmetricMatrix(values, values.length, values[values.length - 1].length);
    }

    /**
     * Creates a {@link Matrix} from the given row {@link Vector}s. No checks for validly formed row {@link Vector}s are
     * done, use with care.
     *
     * @param rowVectors The {@link Vector}s that resemble the rows of the new {@link Matrix}.
     * @return A new {@link Matrix}.
     */
    public static Matrix assembleMatrixFromRows(List<Vector> rowVectors) {
        double[][] elements = new double[rowVectors.size()][rowVectors.get(0).getDimension()];
        for (int column = 0; column < rowVectors.size(); column++) {
            elements[column] = rowVectors.get(column).getElements();
        }
        return FastMatrices.createRegularMatrix(elements);
    }

}
