package de.bioforscher.singa.mathematics.similarities;

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
public class RboScore {

    private final List<?> list1;
    private final List<?> list2;
    private final int limit;
    private final double p;

    private double rbo;

    public RboScore(List<?> list1, List<?> list2, int limit, double p) {
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

        this.rbo = 0.0;

        for (int i = 1; i < this.limit; i++) {

            List<?> subList1 = new ArrayList<>(this.list1.subList(0, i));
            List<?> subList2 = new ArrayList<>(this.list2.subList(0, i));
            subList1.retainAll(subList2);

            double a = (double) subList1.size() / (double) i;

            this.rbo = this.rbo + Math.pow(p, i - 1) * a;
        }

        this.rbo = (1 - p) * this.rbo;
    }

    public double getRbo() {
        return this.rbo;
    }
}
