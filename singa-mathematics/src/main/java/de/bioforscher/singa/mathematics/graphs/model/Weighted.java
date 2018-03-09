package de.bioforscher.singa.mathematics.graphs.model;

/**
 * @author cl
 */
public interface Weighted<WeightType> {

    WeightType getWeight();

    void setWeight(WeightType weight);

}
