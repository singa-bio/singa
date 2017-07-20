package de.bioforscher.singa.features.model;


import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public interface ScalableFeature<FeatureContent> extends Feature<FeatureContent> {

    void scale(Quantity<Time> time, Quantity<Length> space);

    FeatureContent getScaledQuantity();

    FeatureContent getHalfScaledQuantity();

}
