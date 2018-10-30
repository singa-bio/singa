package bio.singa.mathematics.matrices;

import bio.singa.core.utility.Resources;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class MatricesTest {

    @Test
    void shouldReadUnlabeledRegularMatrixFromCSV() throws Exception {
        Path regularMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/regular_matrix.csv"));
        RegularMatrix squareMatrix = Matrices.readUnlabeledMatrixFromCSV(regularMatrixPath).as(RegularMatrix.class);
        assertArrayEquals(new double[]{6.0, 9.0, 15.0, 21.0, 7.0}, squareMatrix.getColumn(2).getElements());
    }

    @Test
    void shouldReadUnlabeledSquareMatrixFromCSV() throws Exception {
        Path squareMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/square_matrix.csv"));
        SquareMatrix squareMatrix = Matrices.readUnlabeledMatrixFromCSV(squareMatrixPath).as(SquareMatrix.class);
        assertArrayEquals(new double[]{4.0, 6.0, 10.0, 14.0}, squareMatrix.getColumn(1).getElements());
    }

    @Test
    void shouldReadLabeledRegularMatrixFromCSV() throws Exception {
        Path labeledRegularMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/labeled_square_matrix.csv"));
        LabeledMatrix<String> labeledRegularMatrix = Matrices.readLabeledMatrixFromCSV(labeledRegularMatrixPath);
        assertArrayEquals(new double[]{4.0, 6.0, 10.0, 14.0}, labeledRegularMatrix.getColumnByLabel("C2").getElements());
    }

    @Test
    void shouldReadUnlabeledSymmetricMatrixFromCSV() throws Exception {
        Path squareMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/symmetric_matrix.csv"));
        SymmetricMatrix symmetricMatrix = Matrices.readUnlabeledMatrixFromCSV(squareMatrixPath).as(SymmetricMatrix.class);
        assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, symmetricMatrix.getRow(3).getElements());
    }

    @Test
    void shouldReadLabeledSymmetricMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/labeled_symmetric_matrix.csv"));
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements());
    }

    @Test
    void shouldReadLabeledLowerTriangularMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/labeled_symmetric_matrix_triangular_lower.csv"));
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements());
    }

    @Test
    void shouldReadLabeledUpperTriangularMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Resources.getResourceAsFileLocation("matrices/labeled_symmetric_matrix_triangular_upper.csv"));
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements());
    }

    @Test
    void shouldNormalizeMatrix() {
        double[][] values = {{1.0, 2.0, 3.0}, {4.0, 5.0, 5.0}, {7.0, 8.0, 9.0}};
        Matrix matrix = new RegularMatrix(values);
        Matrix normalizedMatrix = Matrices.normalize(matrix);
        assertEquals(0.0, normalizedMatrix.getElement(0, 0), 1E-9);
        assertEquals(1.0, normalizedMatrix.getElement(2, 2), 1E-9);
    }
}