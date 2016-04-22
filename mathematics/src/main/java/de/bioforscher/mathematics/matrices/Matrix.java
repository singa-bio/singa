package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.concepts.Dimension;
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

    public RegularVector getColumn(int columnIndex);

    public Dimension getColumnDimension();

    public RegularVector getRow(int rowIndex);

    public Dimension getRowDimension();

    default int getNumberOfColumnDimensions() {
        return getColumnDimension().getDegreesOfFreedom();
    }

    default int getNumberOfRowDimensions() {
        return getRowDimension().getDegreesOfFreedom();
    }

    default boolean hasSameInnerDimension(Matrix matrix) {
        return this.getNumberOfColumnDimensions() == matrix.getNumberOfRowDimensions();
    }

    default void assertThatInnerDimensionsMatch(Matrix matrix) {
        if (!hasSameInnerDimension(matrix)) {
            throw new IncompatibleDimensionsException(this, matrix);
        }
    }

    default boolean hasSameInnerDimension(Vector vector) {
        return this.getNumberOfColumnDimensions() == vector.getDimension().getDegreesOfFreedom();
    }

    default void assertThatInnerDimensionsMatch(Vector vector) {
        if (!hasSameInnerDimension(vector)) {
            throw new IncompatibleDimensionsException(this, vector);
        }
    }

    @Override
    default boolean hasSameDimensions(Matrix matrix) {
        return this.getRowDimension().equals(matrix.getRowDimension())
                && this.getColumnDimension().equals(matrix.getColumnDimension());
    }

}
