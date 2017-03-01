package de.bioforscher.mathematics.metrics.implementations;

import de.bioforscher.mathematics.metrics.model.Metric;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Calculates the distance between two {@code Collection}s, based on a comparison of the the similarity and diversity of
 * both. The Jaccard index is defined as the size of the intersection divided by the size of the union of the sample
 * sets. To infer a metric from the Jaccard index it is necessary to subtract the Index from 1.
 *
 * @param <Type> The type of object that is contained in the collection.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Jaccard_index">Wikipedia: Jaccard index</a>
 */
public class JaccardMetric<Type> implements Metric<Collection<Type>> {

    @Override
    public double calculateDistance(Collection<Type> first, Collection<Type> second) {
        if (first.isEmpty() || second.isEmpty()) {
            return 1.0;
        }
        Set<Object> union = new HashSet<>(first);
        union.addAll(second);
        Set<Object> intersection = new HashSet<>(first);
        intersection.retainAll(second);
        return (double) (union.size() - intersection.size()) / (double) union.size();
    }

}
