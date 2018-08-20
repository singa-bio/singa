package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class FirstOrderBackwardsRateConstant extends FirstOrderRateConstant implements BackwardsRateConstant<FirstOrderRate> {

    private static final String symbol = "k_bwd_1";

    public FirstOrderBackwardsRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(firstOrderRateQuantity, featureOrigin);
    }

    public FirstOrderBackwardsRateConstant(double value, Unit<FirstOrderRate> firstOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(value, firstOrderRateQuantity), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
