package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class ThirdOrderRateConstant extends RateConstant<ThirdOrderRate> {

    public ThirdOrderRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, Evidence evidence) {
        super(thirdOrderRateQuantity, evidence);
    }

    public ThirdOrderRateConstant(Quantity<ThirdOrderRate> quantity) {
        super(quantity);
    }

}
