package de.bioforscher.singa.mathematics;

import de.bioforscher.singa.mathematics.matrices.Matrix;
import org.junit.Assert;

/**
 * @author cl
 */
public class NumberConceptAssertion {

    public static  void assertMatrixEquals(Matrix expected, Matrix actual, double delta) {
        expected.forEach((position, expectedValue) -> {
            int column = position.getFirst();
            int row = position.getSecond();
            double actualValue = actual.getElement(column, row);
            if (doubleIsDifferent(expectedValue, actualValue, delta)) {
                Assert.fail("expected: <" + expectedValue + "> at column " + column + " and row " + row + " but was: <" + actualValue + ">");
            }
        });
    }

    private static boolean doubleIsDifferent(double d1, double d2, double delta) {
        return Double.compare(d1, d2) != 0 && !(Math.abs(d1 - d2) <= delta);
    }
}
