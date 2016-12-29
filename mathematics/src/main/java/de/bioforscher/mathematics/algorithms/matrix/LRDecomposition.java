package de.bioforscher.mathematics.algorithms.matrix;

import de.bioforscher.mathematics.matrices.Matrices;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.RegularMatrix;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 05.10.2016.
 */
public class LRDecomposition {

    public static Matrix calculateRowEchelonMatrix(Matrix originalMatrix) {
        List<Vector> matrixRows = Matrices.divideIntoRows(originalMatrix);
        for (int iteration = 0; iteration < Math.min(originalMatrix.getColumnDimension(), originalMatrix.getRowDimension()); iteration++) {
            // this row is the zero
            // FIXME: this can fail if the very first row is zero
            if (matrixRows.get(iteration).isZero()) {
                continue;
            }
            // search for vector with largest absolute value in the current iteration
            int pivotIndex = getRowWithAbsolutMaximalElementAtIndex(matrixRows, iteration, iteration);
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

    private static int getRowWithAbsolutMaximalElementAtIndex(List<Vector> matrixRows, int index, int startIndex) {
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
        for (Vector row: echelonRows) {
            if (!row.isZero()){
                rank++;
            }
        }
        return rank;
    }



    public static void main(String[] args) {
        // uncritical system of equations
        Matrix a = new RegularMatrix(new double[][]{{2, 1, -1, 8}, {-3, -1, 2, -11}, {-2, 1, 2, -3}});
        System.out.println("A =\n" + a);
        System.out.println("Row Echelon Form =\n" + calculateRowEchelonMatrix(a));
        System.out.println();
        // potential division by zero
        Matrix b = new RegularMatrix(new double[][]{{1, 2 ,3},{0, 5, 4},{0, 10, 2}});
        System.out.println("B =\n" + b);
        System.out.println("Row Echelon Form =\n" + calculateRowEchelonMatrix(b));
        System.out.println();
        // linear dependent vectors
        Matrix c = new RegularMatrix(new double[][]{{1, 2 ,3},{0, 6, 4},{0, 3, 2}});
        System.out.println("C =\n" + c);
        System.out.println("Row Echelon Form =\n" + calculateRowEchelonMatrix(c));
        System.out.println();
        // linear dependent vectors
        Matrix d = new RegularMatrix(new double[][]{{2 ,3},{0, 1},{4, -1}});
        System.out.println("D =\n" + d);
        System.out.println("Row Echelon Form =\n" + calculateRowEchelonMatrix(d));
        System.out.println();


    }

}
