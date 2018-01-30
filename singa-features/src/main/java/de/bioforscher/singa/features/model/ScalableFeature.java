package de.bioforscher.singa.features.model;


import de.bioforscher.singa.features.parameters.EnvironmentalParameters;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public interface ScalableFeature<FeatureContent> extends Feature<FeatureContent> {

    void scale(Quantity<Time> time, Quantity<Length> space);

    default void scale() {
        scale(EnvironmentalParameters.getTimeStep(), EnvironmentalParameters.getNodeDistance());
    }

    FeatureContent getScaledQuantity();

    FeatureContent getHalfScaledQuantity();

}
