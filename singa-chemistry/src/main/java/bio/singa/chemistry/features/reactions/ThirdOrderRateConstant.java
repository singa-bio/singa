package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class ThirdOrderRateConstant extends RateConstant<ThirdOrderRate> {

    public ThirdOrderRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(thirdOrderRateQuantity, featureOrigin);
    }

}
