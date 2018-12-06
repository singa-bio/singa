package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ZeroOrderForwardsRateConstant extends ZeroOrderRateConstant implements ForwardsRateConstant<ZeroOrderRate> {

    private static final String symbol = "K_fwd_0";

    public ZeroOrderForwardsRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, Evidence featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderForwardsRateConstant(double value, Unit<ZeroOrderRate> unit, Evidence featureOrigin) {
        super(value, unit, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
