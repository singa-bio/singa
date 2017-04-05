package de.bioforscher.singa.benchmark.mathematics;

import de.bioforscher.singa.mathematics.matrices.FastMatrices;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.matrices.RegularMatrix;
import de.bioforscher.singa.mathematics.matrices.SymmetricMatrix;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cl
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Timeout(time = Integer.MAX_VALUE)
@Warmup(iterations = 10)
@Measurement(iterations = 5)
public class MatrixBenchmark {

    private static final int numberOfValues = 1000;
    private static final int numberOfRows = 100;
    private static final int numberOfColumns = 100;

    private List<double[][]> regularValues;
    private List<double[][]> compactValues;

    @Setup
    public void setUp() throws IOException {
        this.regularValues = new ArrayList<>();
        this.compactValues = new ArrayList<>();
        for (int iteration = 0; iteration < numberOfValues; iteration++) {
            // prepare values for regular matrices
            double[][] value = new double[numberOfColumns][numberOfRows];
            for (int rowIndex = 0; rowIndex < value.length; rowIndex++) {
                for (int columnIndex = 0; columnIndex < value[rowIndex].length; columnIndex++) {
                    value[rowIndex][columnIndex] = Math.random();
                }
            }
            this.regularValues.add(value);

            // prepare values for symmetric matrices
            double[][] compactValues = new double[numberOfColumns][];
            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                compactValues[rowIndex] = new double[rowIndex + 1];
            }
            for (int rowIndex = 0; rowIndex < compactValues.length; rowIndex++) {
                for (int columnIndex = 0; columnIndex < compactValues[rowIndex].length; columnIndex++) {
                    compactValues[rowIndex][columnIndex] = Math.random();
                }
            }
            this.compactValues.add(compactValues);
        }
    }

    // @Benchmark
    public void benchmarkRegularInitialization() {
        for (double[][] value : this.regularValues) {
            Matrix m = new RegularMatrix(value);
        }
    }

    // @Benchmark
    public void benchmarkFastInitialization() {
        for (double[][] value : this.regularValues) {
            Matrix m = FastMatrices.createRegularMatrix(value);
        }
    }

    @Benchmark
    public void benchmarkRegularSymmetricInitialization() {
        for (double[][] value : this.compactValues) {
            Matrix m = new SymmetricMatrix(value);
        }
    }

    @Benchmark
    public void benchmarkFastSymmetricInitialization() {
        for (double[][] value : this.compactValues) {
            Matrix m = FastMatrices.createSymmetricMatrix(value);
        }
    }


}
