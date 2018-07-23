package bio.singa.mathematics.intervals;

import bio.singa.core.utility.Range;
import bio.singa.mathematics.concepts.Ring;

/**
 * The main focus of interval in an arithmetic context is the simplest way to calculate upper and lower endpoints for
 * the range of values of a function in one or more variables. The given lover and upper bonds are both inclusive.
 *
 * @author cl
 */
public class Interval extends Range<Double> implements Ring<Interval> {

    /**
     * Creates a new interval with the given values.
     * <pre> I = [lowerBound,upperBound]</pre>
     *
     * @param lowerBound The lower bound.
     * @param upperBound The upper bond.
     */
    public Interval(Double lowerBound, Double upperBound) {
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
    public Interval add(Interval summand) {
        final double upperBound = getUpperBound() + summand.getUpperBound();
        final double lowerBound = getLowerBound() + summand.getLowerBound();
        return new Interval(lowerBound, upperBound);
    }

    @Override
    public Interval additivelyInvert() {
        return new Interval(-getLowerBound(), -getUpperBound());
    }

    @Override
    public Interval multiply(Interval multiplicand) {
        double[] minMax = minMax(getLowerBound() * multiplicand.getLowerBound(), getLowerBound() * multiplicand.getUpperBound(),
                getUpperBound() * multiplicand.getLowerBound(), getUpperBound() * multiplicand.getUpperBound());
        return new Interval(minMax[0], minMax[1]);
    }

    @Override
    public Interval subtract(Interval subtrahend) {
        final double upperBound = getUpperBound() - subtrahend.getUpperBound();
        final double lowerBound = getLowerBound() - subtrahend.getLowerBound();
        return new Interval(lowerBound, upperBound);
    }
}
