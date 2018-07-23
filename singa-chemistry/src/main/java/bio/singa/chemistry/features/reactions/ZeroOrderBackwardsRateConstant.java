package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ZeroOrderBackwardsRateConstant extends ZeroOrderRateConstant implements BackwardsRateConstant<ZeroOrderRate> {

    public static final String symbol = "k_bwd_0";

    public ZeroOrderBackwardsRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderBackwardsRateConstant(double value, Unit<ZeroOrderRate> unit, FeatureOrigin featureOrigin) {
        super(value, unit, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
