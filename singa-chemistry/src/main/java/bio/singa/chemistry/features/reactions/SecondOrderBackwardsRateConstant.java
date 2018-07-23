package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class SecondOrderBackwardsRateConstant extends SecondOrderRateConstant implements BackwardsRateConstant<SecondOrderRate> {

    public static final String symbol = "k_bwd_2";

    public SecondOrderBackwardsRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(secondOrderRateQuantity, featureOrigin);
    }

    public SecondOrderBackwardsRateConstant(double value, Unit<SecondOrderRate> unit, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
