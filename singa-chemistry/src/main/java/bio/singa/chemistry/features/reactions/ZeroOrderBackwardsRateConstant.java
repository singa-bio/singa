package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ZeroOrderBackwardsRateConstant extends ZeroOrderRateConstant implements BackwardsRateConstant<ZeroOrderRate> {

    public static final String symbol = "k_bwd_0";

    public ZeroOrderBackwardsRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, Evidence featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderBackwardsRateConstant(double value, Unit<ZeroOrderRate> unit, Evidence featureOrigin) {
        super(value, unit, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
