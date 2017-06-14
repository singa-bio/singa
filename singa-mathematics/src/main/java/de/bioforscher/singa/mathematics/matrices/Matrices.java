package de.bioforscher.singa.mathematics.matrices;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.algorithms.matrix.QRDecomposition;
import de.bioforscher.singa.mathematics.algorithms.matrix.SVDecomposition;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Matrices {

    private static final String DEFAULT_CSV_DELIMITER = ",";

    /**
     * prevent instantiation
     */
    private Matrices() {
    }

    /**
     * Creates a new identity matrix of the desired size. <br>
     * <p>
     * For example the call:
     * <pre>
     * Matrices.generateIdentityMatrix(4) </pre>
     * result in the matrix:
     * <pre>
     * 1.0 0.0 0.0 0.0
     * 0.0 1.0 0.0 0.0
     * 0.0 0.0 1.0 0.0
     * 0.0 0.0 0.0 1.0 </pre>
     *
     * @param size The size of the resulting matrix.
     * @return A identity matrix.
     */
    public static SquareMatrix generateIdentityMatrix(int size) {
        double[][] values = new double[size][size];
        for (int diagonalIndex = 0; diagonalIndex < size; diagonalIndex++) {
            values[diagonalIndex][diagonalIndex] = 1.0;
        }
        return new SquareMatrix(values);
    }

    /**
     * Divides the matrix into columns. The resulting list contains all columns of the original matrix in vectors. The
     * order is maintained (the column with index 0 is in the list at index 0).
     *
     * @param matrix The matrix to be divided.
     * @return The columns as vectors.
     */
    public static List<Vector> divideIntoColumns(Matrix matrix) {
        List<Vector> columns = new ArrayList<>();
        for (int column = 0; column < matrix.getColumnDimension(); column++) {
            columns.add(matrix.getColumn(column));
        }
        return columns;
    }

    /**
     * Creates a new matrix from vectors in a list. Each vector is a new column in the new matrix. The order of the list
     * is maintained.
     *
     * @param columnVectors The columns as vectors.
     * @return The columns combined to a matrix.
     */
    public static Matrix assembleMatrixFromColumns(List<Vector> columnVectors) {
        if (!Vectors.haveSameDimension(columnVectors)) {
            throw new IllegalArgumentException("All vectors need to have the same dimension in order to create a" +
                    " matrix out of them.");
        }
        double[][] elements = new double[columnVectors.size()][columnVectors.get(0).getDimension()];
        for (int row = 0; row < columnVectors.size(); row++) {
            elements[row] = columnVectors.get(row).getElements();
        }
        return new RegularMatrix(elements);
    }

    /**
     * Divides the matrix into rows. The resulting list contains all rows of the original matrix in vectors. The
     * order is maintained (the column with index 0 is in the list at index 0).
     *
     * @param matrix The matrix to be divided.
     * @return The rows as vectors.
     */
    public static List<Vector> divideIntoRows(Matrix matrix) {
        List<Vector> rows = new ArrayList<>();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            rows.add(matrix.getRow(row));
        }
        return rows;
    }

    /**
     * Creates a new matrix from vectors in a list. Each vector is a new row in the new matrix. The order of the list
     * is maintained.
     *
     * @param rowVectors The rows as vectors.
     * @return The rows combined to a matrix.
     */
    public static Matrix assembleMatrixFromRows(List<Vector> rowVectors) {
        if (!Vectors.haveSameDimension(rowVectors)) {
            throw new IllegalArgumentException("All vectors need to have the same dimension in order to create a" +
                    " matrix out of them.");
        }
        double[][] elements = new double[rowVectors.size()][rowVectors.get(0).getDimension()];
        for (int column = 0; column < rowVectors.size(); column++) {
            elements[column] = rowVectors.get(column).getElements();
        }
        return new RegularMatrix(elements);
    }


    /**
     * Returns a list of positions of the minimal elements of a {@link Matrix}
     *
     * @return positions of the minimal elements represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static List<Pair<Integer>> getPositionsOfMinimalElement(Matrix matrix) {
        double minimalElement = Double.MAX_VALUE;
        List<Pair<Integer>> minimalElementPositions = new ArrayList<>();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                double currentMatrixElement = matrix.getElement(i, j);
                if (Double.compare(currentMatrixElement, minimalElement) == 0)
                    minimalElementPositions.add(new Pair<>(i, j));
                else if (Double.compare(currentMatrixElement, minimalElement) < 0) {
                    minimalElement = currentMatrixElement;
                    minimalElementPositions.clear();
                    minimalElementPositions.add(new Pair<>(i, j));
                }
            }
        }
        return minimalElementPositions;
    }

    /**
     * returns an {@link Optional} of a {@link Pair} that represents the position of the unique minimal element,
     * or an empty {@link Optional} if the minimal element is ambiguous
     *
     * @return position of the minimal element represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static Optional<Pair<Integer>> getPositionOfMinimalElement(Matrix matrix) {
        List<Pair<Integer>> minimalElementPositions = getPositionsOfMinimalElement(matrix);
        return minimalElementPositions.size() == 1 ? Optional.of(minimalElementPositions.get(0)) : Optional.empty();
    }

    /**
     * returns a list of positions of the maximal elements of a {@link Matrix}
     *
     * @return positions of the maximal elements represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static List<Pair<Integer>> getPositionsOfMaximalElement(Matrix matrix) {
        double maximalElement = -Double.MAX_VALUE;
        List<Pair<Integer>> maximalElementsPositions = new ArrayList<>();
        for (int i = 0; i < matrix.getColumnDimension(); i++) {
            for (int j = 0; j < matrix.getRowDimension(); j++) {
                double currentMatrixElement = matrix.getElement(i, j);
                if (Double.compare(currentMatrixElement, maximalElement) == 0)
                    maximalElementsPositions.add(new Pair<>(i, j));
                else if (Double.compare(currentMatrixElement, maximalElement) > 0) {
                    maximalElement = currentMatrixElement;
                    maximalElementsPositions.clear();
                    maximalElementsPositions.add(new Pair<>(i, j));
                }
            }
        }
        return maximalElementsPositions;
    }

    /**
     * Returns an {@link Optional} of a {@link Pair} that represents the position of the unique maximal element,
     * or an empty {@link Optional} if the maximal element is ambiguous.
     *
     * @return position of the maximal element represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static Optional<Pair<Integer>> getPositionOfMaximalElement(Matrix matrix) {
        List<Pair<Integer>> maximalElementPositions = getPositionsOfMaximalElement(matrix);
        return maximalElementPositions.size() == 1 ? Optional.of(maximalElementPositions.get(0)) : Optional.empty();
    }

    public static QRDecomposition performQRDecomposition(Matrix matrix) {
        return QRDecomposition.calculateQRDecomposition(matrix);
    }

    public static LabeledMatrix<String> readLabeledMatrixFromCSV(Stream<String> csvLines, String delimiter) {
        List<String[]> rawRows = csvLines
                .map(line -> line.split(delimiter))
                .map(splittedLine -> Arrays.stream(splittedLine)
                        .filter(cell -> !cell.isEmpty())
                        .collect(Collectors.toList()).toArray(new String[0]))
                .collect(Collectors.toList());
        // check if columns are potentially labeled
        List<Integer> distinctRowLengths = rawRows.stream()
                .map(rawRow -> rawRow.length)
                .distinct()
                .collect(Collectors.toList());

        // keep all row lengths for triangular check
        List<Integer> rowLengths = rawRows.stream()
                .map(rawRow -> rawRow.length)
                .collect(Collectors.toList());

        // check for triangular form of symmetric matrix
        boolean triangularUpper = true;
        boolean triangularLower = true;
        for (int i = 1; i < rowLengths.size() - 1; i++) {
            if (rowLengths.get(i + 1) != rowLengths.get(i) + 1) {
                triangularLower = false;
            }
            if (rowLengths.get(i + 1) != rowLengths.get(i) - 1) {
                triangularUpper = false;
            }
        }

        List<String[]> rawColumns = new ArrayList<>();
        if (distinctRowLengths.size() == 2 && (distinctRowLengths.get(0) == distinctRowLengths.get(1) - 1)) {

            List<String> columnLabels = Arrays.asList(rawRows.get(0));
            int rowLength = rawRows.get(1).length;
            for (int i = 0; i < rowLength; i++) {
                String[] rawColumn = new String[rawRows.size() - 1];
                for (int j = 1; j < rawRows.size(); j++) {
                    rawColumn[j - 1] = rawRows.get(j)[i];
                }
                rawColumns.add(rawColumn);
            }
            List<String> rowLabels = Arrays.asList(rawColumns.get(0));
            double[][] values = new double[rawRows.size() - 1][rawColumns.size() - 1];
            for (int i = 1; i < rawRows.size(); i++) {
                for (int j = 1; j < rawColumns.size(); j++) {
                    double value = Double.valueOf(rawRows.get(i)[j]);
                    values[i - 1][j - 1] = value;
                }
            }
            // construct appropriate matrix
            if (SymmetricMatrix.isSymmetric(values)) {
                LabeledSymmetricMatrix<String> labeledSymmetricMatrix = new LabeledSymmetricMatrix<>(values);
                labeledSymmetricMatrix.setColumnLabels(columnLabels);
                return labeledSymmetricMatrix;
            } else {
                LabeledMatrix<String> labeledRegularMatrix = new LabeledRegularMatrix<>(values);
                labeledRegularMatrix.setColumnLabels(columnLabels);
                labeledRegularMatrix.setRowLabels(rowLabels);
                return labeledRegularMatrix;
            }
        } else if (triangularLower || triangularUpper) {
            // labels have to be symmetric if triangular matrix
            List<String> columnLabels = Arrays.asList(rawRows.get(0));
            double[][] values = new double[rawRows.size() - 1][rawRows.size() - 1];
            if (triangularLower) {
                for (int i = rawRows.size() - 1; i > 0; i--) {
                    for (int j = 1; j < i + 1; j++) {
                        double value = Double.valueOf(rawRows.get(i)[j]);
                        values[i - 1][j - 1] = value;
                        values[j - 1][i - 1] = value;
                    }
                }
            } else {
                for (int i = 1; i < rawRows.size(); i++) {
                    for (int j = 1; j < rawRows.size() - i + 1; j++) {
                        double value = Double.valueOf(rawRows.get(i)[j]);
                        values[i - 1][j + i - 2] = value;
                        values[j + i - 2][i - 1] = value;
                    }
                }
            }
            if (SymmetricMatrix.isSymmetric(values)) {
                LabeledSymmetricMatrix<String> labeledSymmetricMatrix = new LabeledSymmetricMatrix<>(values);
                labeledSymmetricMatrix.setColumnLabels(columnLabels);
                return labeledSymmetricMatrix;
            } else {
                throw new IllegalArgumentException("triangular matrix has to be symmetric");
            }
        } else {
            throw new IllegalArgumentException("labeling seems to be incorrect");
        }
    }

    public static Matrix readUnlabeledMatrixFromCSV(Stream<String> csvLines, String delimiter) {
        List<String[]> rawRows = csvLines.map(line -> line.split(delimiter))
                .map(splittedLine -> Arrays.stream(splittedLine)
                        .filter(cell -> !cell.isEmpty())
                        .toArray(String[]::new))
                .collect(Collectors.toList());
        // check if rows are all of same length
        boolean rowsOfSameLength = rawRows.stream()
                .map(rawRow -> rawRow.length)
                .distinct()
                .count() == 1;
        if (!rowsOfSameLength) {
            throw new IllegalArgumentException("rows seem to contain missing values");
        }
        int rowLength = rawRows.get(0).length;
        List<String[]> rawColumns = new ArrayList<>();
        for (int i = 0; i < rowLength; i++) {
            String[] rawColumn = new String[rawRows.size()];
            for (int j = 0; j < rawRows.size(); j++) {
                rawColumn[j] = rawRows.get(j)[i];
            }
            rawColumns.add(rawColumn);
        }
        // check if columns are all of same length
        boolean columnsOfSameLength = rawColumns.stream()
                .map(rawColumn -> rawColumn.length)
                .distinct()
                .count() == 1;
        if (!columnsOfSameLength) {
            throw new IllegalArgumentException("columns seem to contain missing values");
        }
        // construct double array
        double[][] values = new double[rawRows.size()][rawColumns.size()];
        for (int i = 0; i < rawRows.size(); i++) {
            for (int j = 0; j < rawColumns.size(); j++) {
                double value = Double.valueOf(rawRows.get(i)[j]);
                values[i][j] = value;
            }
        }

        // construct appropriate matrix
        if (SymmetricMatrix.isSymmetric(values)) {
            return new SymmetricMatrix(values);
        } else if (SquareMatrix.isSquare(values)) {
            return new SquareMatrix(values);
        } else {
            return new RegularMatrix(values);
        }
    }

    public static LabeledMatrix<String> readLabeledMatrixFromCSV(Path path) throws IOException {
        return readLabeledMatrixFromCSV(Files.lines(path), DEFAULT_CSV_DELIMITER);
    }

    public static LabeledMatrix<String> readLabeledMatrixFromCSV(Path path, String delimiter) throws IOException {
        return readLabeledMatrixFromCSV(Files.lines(path), delimiter);
    }

    public static Matrix readUnlabeledMatrixFromCSV(Path path, String delimiter) throws IOException {
        return readUnlabeledMatrixFromCSV(Files.lines(path), delimiter);
    }

    public static Matrix readUnlabeledMatrixFromCSV(Path path) throws IOException {
        return readUnlabeledMatrixFromCSV(path, DEFAULT_CSV_DELIMITER);
    }

    /**
     * calculates the covariance matrix between to matrices in respect to matrix A
     * cov(A) = B'*A
     *
     * @param a matrix A to which covariance should be calculated
     * @param b matrix
     * @return the covariance matrix (A and B are not modified)
     */
    public static Matrix calculateCovarianceMatrix(Matrix a, Matrix b) {
        return b.transpose().multiply(a);
    }

    public static SVDecomposition performSVDecomposition(Matrix matrix) {
        return new SVDecomposition(matrix);
    }

}
