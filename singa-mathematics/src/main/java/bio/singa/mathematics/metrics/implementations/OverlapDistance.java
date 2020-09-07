package bio.singa.mathematics.metrics.implementations;

import bio.singa.mathematics.intervals.IntegerInterval;
import bio.singa.mathematics.metrics.model.Metric;

/**
 * Jaccard-like distance between intervals
 *
 * @author cl
 */
public class OverlapDistance implements Metric<IntegerInterval> {

    @Override
    public double calculateDistance(IntegerInterval first, IntegerInterval second) {
        IntegerInterval intersection = first.intersection(second);
        int intersectionSize;
        if (intersection == null) {
            return 1;
        } else {
            intersectionSize = intersection.size();
        }
        int unionSize = first.union(second).size();
        return (double) (unionSize - intersectionSize) / (unionSize);
    }

}
