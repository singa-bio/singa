package bio.singa.mathematics.algorithms.matrix;

import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;

import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class LUDecomposition {

    public static Matrix calculateRowEchelonMatrix(Matrix originalMatrix) {
        List<Vector> matrixRows = Matrices.divideIntoRows(originalMatrix);
        for (int iteration = 0; iteration < Math.min(originalMatrix.getColumnDimension(), originalMatrix.getRowDimension()); iteration++) {
            // this row is the zero
            // FIXME: this can fail if the very first row is zero
            if (matrixRows.get(iteration).isZero()) {
                continue;
            }
            // search for vector with largest absolute value in the current iteration
            int pivotIndex = getRowWithAbsoluteMaximalElementAtIndex(matrixRows, iteration, iteration);
            // if this element is zero
            if (matrixRows.get(pivotIndex).getElement(iteration) == 0) {
                // no decomposition possible
                throw new IllegalStateException("The given matrix is singular - no LU decomposition possible.");
            }
            // swap those rows in the row list
            Collections.swap(matrixRows, iteration, pivotIndex);
            // start factor calculation
            for (int row = iteration + 1; row < originalMatrix.getRowDimension(); row++) {
                if (matrixRows.get(iteration).getElement(iteration) != 0) {
                    double[] newRowValues = new double[originalMatrix.getColumnDimension()];
                    // calculate division factor
                    double factor = matrixRows.get(row).getElement(iteration) / matrixRows.get(iteration).getElement(iteration);
                    for (int column = iteration + 1; column < originalMatrix.getColumnDimension(); column++) {
                        // subtract and scale by factor
                        newRowValues[column] = matrixRows.get(row).getElement(column) - matrixRows.get(iteration).getElement(column) * factor;
                    }
                    newRowValues[iteration] = 0;
                    matrixRows.set(row, new RegularVector(newRowValues));
                }
            }

        }
        return Matrices.assembleMatrixFromRows(matrixRows);
    }

    private static int getRowWithAbsoluteMaximalElementAtIndex(List<Vector> matrixRows, int index, int startIndex) {
        double maximalValue = -Double.MAX_VALUE;
        int maximalIndex = 0;
        for (int row = startIndex; row < matrixRows.size(); row++) {
            double currentValue = Math.abs(matrixRows.get(row).getElement(index));
            if (currentValue > maximalValue) {
                maximalValue = currentValue;
                maximalIndex = row;
            }
        }
        return maximalIndex;
    }

    public static int getRank(Matrix matrix) {
        List<Vector> echelonRows = Matrices.divideIntoRows(calculateRowEchelonMatrix(matrix));
        // count non-zero rows
        int rank = 0;
        for (Vector row : echelonRows) {
            if (!row.isZero()) {
                rank++;
            }
        }
        return rank;
    }

}
