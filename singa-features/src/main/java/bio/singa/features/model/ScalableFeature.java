package bio.singa.features.model;


import bio.singa.features.parameters.Environment;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public interface ScalableFeature<FeatureContent extends Quantity<FeatureContent>> extends Feature<Quantity<FeatureContent>> {

    void scale(Quantity<Time> time, Quantity<Length> space);

    default void scale(Quantity<Time> time) {
        scale(time, Environment.getNodeDistance());
    }

    default void scale() {
        scale(Environment.getTimeStep(), Environment.getNodeDistance());
    }

    Quantity<FeatureContent> getScaledQuantity();

    Quantity<FeatureContent> getHalfScaledQuantity();

}
