package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;

/**
 * Created by fkaiser on 19.10.16.
 */
public class SVDecomposition {

    private final Matrix matrix;
    private Matrix v;
    private Matrix ut;
    private Matrix u;

    public SVDecomposition(Matrix matrix) {

        this.matrix = matrix;
    }

    public Matrix getV() {
        return v;
    }

    public Matrix getUT() {
        return ut;
    }

    public Matrix getU() {
        return u;
    }
}
