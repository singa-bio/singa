package bio.singa.chemistry.features.reactions;


import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class FirstOrderForwardsRateConstant extends FirstOrderRateConstant implements ForwardsRateConstant<FirstOrderRate> {

    private static final String symbol = "k_fwd_1";

    public FirstOrderForwardsRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, Evidence featureOrigin) {
        super(firstOrderRateQuantity, featureOrigin);
    }

    public FirstOrderForwardsRateConstant(double value, Unit<FirstOrderRate> unit, Evidence featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
