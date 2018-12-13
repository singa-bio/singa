package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ThirdOrderForwardsRateConstant extends ThirdOrderRateConstant implements ForwardsRateConstant<ThirdOrderRate> {

    public static final String symbol = "k_fwd_3";

    public ThirdOrderForwardsRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, Evidence evidence) {
        super(thirdOrderRateQuantity, evidence);
    }

    public ThirdOrderForwardsRateConstant(double value, Unit<ThirdOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    @Override
    public String getDescriptor() {
        return symbol;
    }


}
