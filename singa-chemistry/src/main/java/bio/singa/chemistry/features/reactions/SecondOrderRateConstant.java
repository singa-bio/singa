package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;

/**
 * @author cl
 */
public abstract class SecondOrderRateConstant extends RateConstant<SecondOrderRate> {

    public SecondOrderRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(secondOrderRateQuantity, featureOrigin);
    }

}
