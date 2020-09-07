package bio.singa.mathematics;

import bio.singa.mathematics.intervals.DoubleInterval;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.Vector;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author cl
 */
public class NumberConceptAssertion {

    public static void assertMatrixEquals(Matrix expected, Matrix actual, double delta) {
        if (!expected.hasSameDimensions(actual)) {
            fail("expected dimension: <" + expected.getDimensionAsString() + "> but was: <" + actual.getDimensionAsString() + ">");
        }
        expected.forEach((position, expectedValue) -> {
            int column = position.getFirst();
            int row = position.getSecond();
            double actualValue = actual.getElement(column, row);
            if (doubleIsDifferent(expectedValue, actualValue, delta)) {
                fail("expected: <" + expectedValue + "> at column " + column + " and row " + row + " but was: <" + actualValue + ">");
            }
        });
    }

    public static void assertVectorEquals(Vector expected, Vector actual, double delta) {
        if (expected.getDimension() != actual.getDimension()) {
            fail("expected dimension: <" + expected.getDimension() + "> but was: <" + actual.getDimension() + ">");
        }
        for (int index = 0; index < expected.getDimension(); index++) {
            double expectedValue = expected.getElement(index);
            double actualValue = actual.getElement(index);
            if (doubleIsDifferent(expectedValue, actualValue, delta)) {
                fail("expected: <" + expectedValue + "> at index " + index + " but was: <" + actualValue + ">");
            }
        }
    }

    public static void assertIntervalEquals(DoubleInterval expected, DoubleInterval actual, double delta) {
        double expectedLowerBond = expected.getLowerBound();
        double actualLowerBond = actual.getLowerBound();
        if (doubleIsDifferent(expectedLowerBond, actualLowerBond, delta)) {
            fail("expected: <" + expectedLowerBond + "> as lower bond but was: <" + actualLowerBond + ">");
        }
        double expectedUpperBond = expected.getUpperBound();
        double actualUpperBond = actual.getUpperBound();
        if (doubleIsDifferent(expectedUpperBond, actualUpperBond, delta)) {
            fail("expected: <" + expectedUpperBond + "> as upper bond but was: <" + actualUpperBond + ">");
        }
    }

    private static boolean doubleIsDifferent(double d1, double d2, double delta) {
        return Double.compare(d1, d2) != 0 && !(Math.abs(d1 - d2) <= delta);
    }


}