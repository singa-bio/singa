package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ThirdOrderBackwardsRateConstant extends ThirdOrderRateConstant implements BackwardsRateConstant<ThirdOrderRate> {

    public static final String symbol = "k_bwd_3";

    public ThirdOrderBackwardsRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, Evidence featureOrigin) {
        super(thirdOrderRateQuantity, featureOrigin);
    }

    public ThirdOrderBackwardsRateConstant(double value, Unit<ThirdOrderRate> unit, Evidence featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
