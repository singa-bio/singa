package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class SecondOrderForwardsRateConstant extends SecondOrderRateConstant implements ForwardsRateConstant<SecondOrderRate> {

    public SecondOrderForwardsRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, Evidence evidence) {
        super(secondOrderRateQuantity, evidence);
    }

    public SecondOrderForwardsRateConstant(double value, Unit<SecondOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    public SecondOrderForwardsRateConstant(Quantity<SecondOrderRate> quantity) {
        super(quantity);
    }

}
