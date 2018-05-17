package de.bioforscher.singa.chemistry.descriptive.features.reactions;

import de.bioforscher.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ZeroOrderForwardsRateConstant extends ZeroOrderRateConstant implements ForwardsRateConstant<ZeroOrderRate> {

    private static final String symbol = "K_fwd_0";

    public ZeroOrderForwardsRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderForwardsRateConstant(double value, Unit<ZeroOrderRate> unit, FeatureOrigin featureOrigin) {
        super(value, unit, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
