package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class SecondOrderBackwardsRateConstant extends SecondOrderRateConstant implements BackwardsRateConstant<SecondOrderRate> {

    public SecondOrderBackwardsRateConstant(Quantity<SecondOrderRate> secondOrderRateQuantity, Evidence evidence) {
        super(secondOrderRateQuantity, evidence);
    }

    public SecondOrderBackwardsRateConstant(double value, Unit<SecondOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    public SecondOrderBackwardsRateConstant(Quantity<SecondOrderRate> quantity) {
        super(quantity);
    }

}
