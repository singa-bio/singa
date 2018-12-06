package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ThirdOrderForwardsRateConstant extends ThirdOrderRateConstant implements ForwardsRateConstant<ThirdOrderRate> {

    public static final String symbol = "k_fwd_3";

    public ThirdOrderForwardsRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(thirdOrderRateQuantity, featureOrigin);
    }

    public ThirdOrderForwardsRateConstant(double value, Unit<ThirdOrderRate> unit, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }


}