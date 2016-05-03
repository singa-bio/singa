package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.concepts.MultiDimensional;
import de.bioforscher.mathematics.concepts.Ring;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

public interface Matrix extends MultiDimensional<Matrix>, Ring<Matrix> {

    <M extends Matrix> M as(Class<M> matrixClass);

    Matrix transpose();

    Matrix multiply(double multiplicand);

    Vector multiply(Vector multiplicand);

    Matrix hadamardMultiply(Matrix multiplicand);

    double[][] getElements();

    double getElement(int rowIndex, int columnIndex);

    RegularVector getColumn(int columnIndex);

    int getColumnDimension();

    RegularVector getRow(int rowIndex);

    int getRowDimension();

    default boolean hasSameInnerDimension(Matrix matrix) {
        return this.getColumnDimension() == matrix.getRowDimension();
    }

    default void assertThatInnerDimensionsMatch(Matrix matrix) {
        if (!hasSameInnerDimension(matrix)) {
            throw new IncompatibleDimensionsException(this, matrix);
        }
    }

    default boolean hasSameInnerDimension(Vector vector) {
        return this.getColumnDimension() == vector.getDimension();
    }

    default void assertThatInnerDimensionsMatch(Vector vector) {
        if (!hasSameInnerDimension(vector)) {
            throw new IncompatibleDimensionsException(this, vector);
        }
    }

    @Override
    default boolean hasSameDimensions(Matrix matrix) {
        return this.getRowDimension() == matrix.getRowDimension()
                && this.getColumnDimension() == matrix.getColumnDimension();
    }

}
