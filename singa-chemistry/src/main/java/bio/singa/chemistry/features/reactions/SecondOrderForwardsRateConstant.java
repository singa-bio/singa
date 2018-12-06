package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class SecondOrderForwardsRateConstant extends SecondOrderRateConstant implements ForwardsRateConstant<SecondOrderRate> {

    public static final String symbol = "k_fwd_2";

    public SecondOrderForwardsRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, Evidence featureOrigin) {
        super(secondOrderRateQuantity, featureOrigin);
    }

    public SecondOrderForwardsRateConstant(double value, Unit<SecondOrderRate> unit, Evidence featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
