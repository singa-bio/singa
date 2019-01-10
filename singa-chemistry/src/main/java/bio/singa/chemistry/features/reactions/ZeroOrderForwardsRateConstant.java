package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ZeroOrderForwardsRateConstant extends ZeroOrderRateConstant implements ForwardsRateConstant<ZeroOrderRate> {

    public ZeroOrderForwardsRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, Evidence evidence) {
        super(zeroOrderRateQuantity, evidence);
    }

    public ZeroOrderForwardsRateConstant(double value, Unit<ZeroOrderRate> unit, Evidence evidence) {
        super(value, unit, evidence);
    }

    public ZeroOrderForwardsRateConstant(Quantity<ZeroOrderRate> quantity) {
        super(quantity);
    }

}
