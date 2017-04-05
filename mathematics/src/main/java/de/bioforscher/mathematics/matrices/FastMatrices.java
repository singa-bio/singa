package de.bioforscher.mathematics.matrices;

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
    public static Matrix createRegularMatrix(double[][] values) {
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
    public static Matrix createSymmetricMatrix(double[][] values) {
        return new SymmetricMatrix(values, values.length, values[values.length - 1].length);
    }

}
