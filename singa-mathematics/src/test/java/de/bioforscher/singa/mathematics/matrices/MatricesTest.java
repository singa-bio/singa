package de.bioforscher.singa.mathematics.matrices;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author fk
 */
public class MatricesTest {

    @Test
    public void shouldReadUnlabeledRegularMatrixFromCSV() throws Exception {
        Path regularMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/regular_matrix.csv").toURI());
        RegularMatrix squareMatrix = Matrices.readUnlabeledMatrixFromCSV(regularMatrixPath).as(RegularMatrix.class);
        Assert.assertArrayEquals(new double[]{6.0, 9.0, 15.0, 21.0, 7.0}, squareMatrix.getColumn(2).getElements(), 0.0);
    }

    @Test
    public void shouldReadUnlabeledSquareMatrixFromCSV() throws Exception {
        Path squareMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/square_matrix.csv").toURI());
        SquareMatrix squareMatrix = Matrices.readUnlabeledMatrixFromCSV(squareMatrixPath).as(SquareMatrix.class);
        assertArrayEquals(new double[]{4.0, 6.0, 10.0, 14.0}, squareMatrix.getColumn(1).getElements(), 0.0);
    }

    @Test
    public void shouldReadLabeledRegularMatrixFromCSV() throws Exception {
        Path labeledRegularMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/labeled_square_matrix.csv").toURI());
        LabeledMatrix<String> labeledRegularMatrix = Matrices.readLabeledMatrixFromCSV(labeledRegularMatrixPath);
        Assert.assertArrayEquals(new double[]{4.0, 6.0, 10.0, 14.0}, labeledRegularMatrix.getColumnByLabel("C2").getElements(), 0.0);
    }

    @Test
    public void shouldReadUnlabeledSymmetricMatrixFromCSV() throws Exception {
        Path squareMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/symmetric_matrix.csv").toURI());
        SymmetricMatrix symmetricMatrix = Matrices.readUnlabeledMatrixFromCSV(squareMatrixPath).as(SymmetricMatrix.class);
        assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, symmetricMatrix.getRow(3).getElements(), 0.0);
    }

    @Test
    public void shouldReadLabeledSymmetricMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/labeled_symmetric_matrix.csv").toURI());
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        Assert.assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements(), 0.0);
    }

    @Test
    public void shouldReadLabeledLowerTriangularMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/labeled_symmetric_matrix_triangular_lower.csv").toURI());
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        Assert.assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements(), 0.0);
    }

    @Test
    public void shouldReadLabeledUpperTriangularMatrixFromCSV() throws Exception {
        Path labeledSymmetricMatrixPath = Paths.get(Thread.currentThread()
                .getContextClassLoader().getResource("matrices/labeled_symmetric_matrix_triangular_upper.csv").toURI());
        LabeledMatrix<String> labeledSymmetricMatrix = Matrices.readLabeledMatrixFromCSV(labeledSymmetricMatrixPath);
        Assert.assertArrayEquals(new double[]{8.0, 14.0, 20.0, 28.0}, labeledSymmetricMatrix.getColumnByLabel("L4").getElements(), 0.0);
    }
}