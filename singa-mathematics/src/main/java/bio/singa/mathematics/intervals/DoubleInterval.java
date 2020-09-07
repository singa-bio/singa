package bio.singa.mathematics.intervals;

import bio.singa.core.utility.Range;
import bio.singa.mathematics.concepts.Ring;

/**
 * The main focus of interval in an arithmetic context is the simplest way to calculate upper and lower endpoints for
 * the range of values of a function in one or more variables. The given lover and upper bonds are both inclusive.
 *
 * @author cl
 */
public class DoubleInterval extends Range<Double> implements Ring<DoubleInterval> {

    /**
     * Creates a new interval with the given values.
     * <pre> I = [lowerBound,upperBound]</pre>
     *
     * @param lowerBound The lower bound.
     * @param upperBound The upper bond.
     */
    public DoubleInterval(Double lowerBound, Double upperBound) {
        super(lowerBound, upperBound);
    }

    private static double[] minMax(double first, double second, double third, double fourth) {
        if (first > second) {
            double temp = first;
            first = second;
            second = temp;
        }
        if (third > fourth) {
            double temp = third;
            third = fourth;
            fourth = temp;
        }
        double min = first < third ? first : third;
        double max = second > fourth ? second : fourth;
        return new double[]{min, max};
    }

    @Override
    public DoubleInterval add(DoubleInterval summand) {
        final double upperBound = getUpperBound() + summand.getUpperBound();
        final double lowerBound = getLowerBound() + summand.getLowerBound();
        return new DoubleInterval(lowerBound, upperBound);
    }

    @Override
    public DoubleInterval additivelyInvert() {
        return new DoubleInterval(-getLowerBound(), -getUpperBound());
    }

    @Override
    public DoubleInterval multiply(DoubleInterval multiplicand) {
        double[] minMax = minMax(getLowerBound() * multiplicand.getLowerBound(), getLowerBound() * multiplicand.getUpperBound(),
                getUpperBound() * multiplicand.getLowerBound(), getUpperBound() * multiplicand.getUpperBound());
        return new DoubleInterval(minMax[0], minMax[1]);
    }

    @Override
    public DoubleInterval subtract(DoubleInterval subtrahend) {
        final double upperBound = getUpperBound() - subtrahend.getUpperBound();
        final double lowerBound = getLowerBound() - subtrahend.getLowerBound();
        return new DoubleInterval(lowerBound, upperBound);
    }
}
