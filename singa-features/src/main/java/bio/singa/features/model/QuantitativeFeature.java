package bio.singa.features.model;

import javax.measure.Quantity;

/**
 * @author cl
 */
public interface QuantitativeFeature<FeatureContent extends Quantity<FeatureContent>> extends Feature<Quantity<FeatureContent>> {
}
