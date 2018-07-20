package bio.singa.mathematics.similarities;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of the RBO score without weights and probabilistic estimations according to:
 * <pre>
 *     Webber, William, Alistair Moffat, and Justin Zobel. "A similarity measure for indefinite rankings."
 *     ACM Transactions on Information Systems (TOIS) 28.4 (2010): 20.
 * </pre>
 *
 * @author fk
 */
public class RankBiasedOverlap {

    private final List<?> list1;
    private final List<?> list2;
    private final int limit;
    private final double p;

    private double rbo;

    public RankBiasedOverlap(List<?> list1, List<?> list2, int limit, double p) {
        this.list1 = list1;
        this.list2 = list2;
        this.limit = limit;
        this.p = p;
        if (limit > list1.size() || limit > list2.size()) {
            throw new IllegalArgumentException("The limit cannot exceed the list sizes.");
        }
        calculateRbo();
    }

    private void calculateRbo() {

        rbo = 0.0;

        for (int i = 1; i < limit; i++) {

            List<?> subList1 = new ArrayList<>(list1.subList(0, i));
            List<?> subList2 = new ArrayList<>(list2.subList(0, i));
            subList1.retainAll(subList2);

            double a = (double) subList1.size() / (double) i;

            rbo = rbo + Math.pow(p, i - 1) * a;
        }

        rbo = (1 - p) * rbo;
    }

    public double getRbo() {
        return rbo;
    }
}
