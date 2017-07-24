package de.bioforscher.singa.mathematics.intervals;

import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.mathematics.concepts.Ring;

/**
 * The main focus of interval in an arithmetic context is the simplest way to calculate upper and lower endpoints for
 * the range of values of a function in one or more variables. The given lover and upper bonds are both inclusive.
 *
 * @author cl
 */
public class Interval extends Range<Double> implements Ring<Interval> {

    /**
     * Creates a new interval with the given values.
     * <pre> I = [lowerBound,upperBound]
     * @param lowerBound The lower bound.
     * @param upperBound The upper bond.
     */
    public Interval(Double lowerBound, Double upperBound) {
        super(lowerBound, upperBound);
    }

    @Override
    public Interval add(Interval summand) {
        final double upperBound = this.getUpperBound() + summand.getUpperBound();
        final double lowerBound = this.getLowerBound() + summand.getLowerBound();
        return new Interval(lowerBound, upperBound);
    }

    @Override
    public Interval additivelyInvert() {
        return new Interval(-this.getLowerBound(), -this.getUpperBound());
    }

    @Override
    public Interval multiply(Interval multiplicand) {
        double[] minMax = minMax(this.getLowerBound() * multiplicand.getLowerBound(), this.getLowerBound() * multiplicand.getUpperBound(),
                this.getUpperBound() * multiplicand.getLowerBound(), this.getUpperBound() * multiplicand.getUpperBound());
        return new Interval(minMax[0], minMax[1]);
    }

    @Override
    public Interval subtract(Interval subtrahend) {
        final double upperBound = this.getUpperBound() - subtrahend.getUpperBound();
        final double lowerBound = this.getLowerBound() - subtrahend.getLowerBound();
        return new Interval(lowerBound, upperBound);
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
        return new double[] {min, max};
    }
}
