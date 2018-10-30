package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;

/**
 * @author cl
 */
public abstract class FirstOrderRateConstant extends RateConstant<FirstOrderRate> {

    protected FirstOrderRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(firstOrderRateQuantity, featureOrigin);
    }

}
