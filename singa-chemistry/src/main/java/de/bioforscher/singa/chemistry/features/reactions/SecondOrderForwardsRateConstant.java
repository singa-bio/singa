package de.bioforscher.singa.chemistry.features.reactions;

import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class SecondOrderForwardsRateConstant extends SecondOrderRateConstant implements ForwardsRateConstant<SecondOrderRate> {

    public static final String symbol = "k_fwd_2";

    public SecondOrderForwardsRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(secondOrderRateQuantity, featureOrigin);
    }

    public SecondOrderForwardsRateConstant(double value, Unit<SecondOrderRate> unit, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
