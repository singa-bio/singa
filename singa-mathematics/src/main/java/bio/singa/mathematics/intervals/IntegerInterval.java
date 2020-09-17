package bio.singa.mathematics.intervals;

import bio.singa.core.utility.Range;
import bio.singa.mathematics.concepts.Ring;
import bio.singa.mathematics.metrics.implementations.OverlapDistance;
import bio.singa.mathematics.metrics.model.Metric;
import bio.singa.mathematics.metrics.model.Metrizable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class IntegerInterval extends Range<Integer> implements Ring<IntegerInterval>, Metrizable<IntegerInterval> {

    private static OverlapDistance distance = new OverlapDistance();


    /**
     * Creates a new interval with the given values.
     * <pre> I = [lowerBound,upperBound]</pre>
     *
     * @param lowerBound The lower bound.
     * @param upperBound The upper bond.
     */
    public IntegerInterval(Integer lowerBound, Integer upperBound) {
        super(lowerBound, upperBound);
    }

    public static IntegerInterval of(int lowerBound, int upperBound) {
        return new IntegerInterval(lowerBound, upperBound);
    }

    public static IntegerInterval of(int bond) {
        return of(bond, bond);
    }

    private static int[] minMax(int first, int second, int third, int fourth) {
        if (first > second) {
            int temp = first;
            first = second;
            second = temp;
        }
        if (third > fourth) {
            int temp = third;
            third = fourth;
            fourth = temp;
        }
        int min = first < third ? first : third;
        int max = second > fourth ? second : fourth;
        return new int[]{min, max};
    }

    public static List<IntegerInterval> determineConsecutiveRanges(Collection<Integer> integers) {
        ArrayList<Integer> positions = new ArrayList<>(integers);
        positions.sort(Integer::compareTo);
        int length = 1;
        List<IntegerInterval> list = new ArrayList<>();
        // If the array is empty, return the list
        if (positions.size() == 0) {
            return list;
        }
        // Traverse the array from first position
        for (int i = 1; i <= positions.size(); i++) {
            // Check the difference between the current and the previous elements
            // If the difference doesn't equal to 1 just increment the length variable.
            if (i == positions.size() || positions.get(i) - positions.get(i - 1) != 1) {
                // If the range contains only one element add it into the list.
                if (length == 1) {
                    list.add(new IntegerInterval(positions.get(i - length), positions.get(i - length)));
                } else {
                    // Build the range between the first element of the range and the current previous element as the last range.
                    list.add(new IntegerInterval(positions.get(i - length), positions.get(i - 1)));
                }
                // After finding the first range initialize the length by 1 to build the next range.
                length = 1;
            } else {
                length++;
            }
        }
        return list;
    }

    @Override
    public IntegerInterval add(IntegerInterval summand) {
        final int upperBound = getUpperBound() + summand.getUpperBound();
        final int lowerBound = getLowerBound() + summand.getLowerBound();
        return new IntegerInterval(lowerBound, upperBound);
    }

    @Override
    public IntegerInterval additivelyInvert() {
        return new IntegerInterval(-getLowerBound(), -getUpperBound());
    }

    @Override
    public IntegerInterval multiply(IntegerInterval multiplicand) {
        int[] minMax = minMax(getLowerBound() * multiplicand.getLowerBound(), getLowerBound() * multiplicand.getUpperBound(),
                getUpperBound() * multiplicand.getLowerBound(), getUpperBound() * multiplicand.getUpperBound());
        return new IntegerInterval(minMax[0], minMax[1]);
    }

    @Override
    public IntegerInterval subtract(IntegerInterval subtrahend) {
        final int upperBound = getUpperBound() - subtrahend.getUpperBound();
        final int lowerBound = getLowerBound() - subtrahend.getLowerBound();
        return new IntegerInterval(lowerBound, upperBound);
    }

    public IntegerInterval intersection(IntegerInterval range) {
        int thisLow = getLowerBound();
        int thisUp = getUpperBound();
        int thatLow = range.getLowerBound();
        int thatUp = range.getUpperBound();
        // they are equal
        if (equals(range)) {
            return new IntegerInterval(thisLow, thisUp);
        }
        int low = Math.max(thisLow, thatLow);
        int up = Math.min(thisUp, thatUp);
        if (low > up) {
            return null;
        } else {
            return new IntegerInterval(low, up);
        }
    }

    public IntegerInterval union(IntegerInterval range) {
        int thisLow = getLowerBound();
        int thisUp = getUpperBound();
        int thatLow = range.getLowerBound();
        int thatUp = range.getUpperBound();
        // they are equal
        if (equals(range)) {
            return new IntegerInterval(thisLow, thisUp);
        }
        int low = Math.min(thisLow, thatLow);
        int up = Math.max(thisUp, thatUp);
        return new IntegerInterval(low, up);
    }

    @Override
    public double distanceTo(IntegerInterval another) {
        return distance.calculateDistance(this, another);
    }

    @Override
    public double distanceTo(IntegerInterval another, Metric<IntegerInterval> metric) {
        return metric.calculateDistance(this, another);
    }

    public int size() {
        return getUpperBound() - getLowerBound() + 1;
    }

}
