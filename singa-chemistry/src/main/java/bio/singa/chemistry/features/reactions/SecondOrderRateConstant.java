package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;

/**
 * @author cl
 */
public abstract class SecondOrderRateConstant extends RateConstant<SecondOrderRate> {

    public SecondOrderRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, Evidence evidence) {
        super(secondOrderRateQuantity, evidence);
    }

}
