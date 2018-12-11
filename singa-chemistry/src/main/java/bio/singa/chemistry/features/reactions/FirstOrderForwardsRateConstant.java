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

    public FirstOrderForwardsRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, Evidence evidence) {
        super(firstOrderRateQuantity, evidence);
    }

    public FirstOrderForwardsRateConstant(double value, Unit<FirstOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
