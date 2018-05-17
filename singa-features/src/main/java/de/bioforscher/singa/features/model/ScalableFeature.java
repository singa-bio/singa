package de.bioforscher.singa.features.model;


import de.bioforscher.singa.features.parameters.EnvironmentalParameters;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public interface ScalableFeature<FeatureContent extends Quantity<FeatureContent>> extends Feature<Quantity<FeatureContent>> {

    void scale(Quantity<Time> time, Quantity<Length> space);

    default void scale(Quantity<Time> time) {
        scale(time, EnvironmentalParameters.getNodeDistance());
    }

    default void scale() {
        scale(EnvironmentalParameters.getTimeStep(), EnvironmentalParameters.getNodeDistance());
    }

    Quantity<FeatureContent> getScaledQuantity();

    Quantity<FeatureContent> getHalfScaledQuantity();

}
