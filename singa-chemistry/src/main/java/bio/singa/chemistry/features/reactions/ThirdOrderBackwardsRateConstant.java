package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class ThirdOrderBackwardsRateConstant extends ThirdOrderRateConstant implements BackwardsRateConstant<ThirdOrderRate> {

    public ThirdOrderBackwardsRateConstant(Quantity<ThirdOrderRate> thirdOrderRateQuantity, Evidence evidence) {
        super(thirdOrderRateQuantity, evidence);
    }

    public ThirdOrderBackwardsRateConstant(double value, Unit<ThirdOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    public ThirdOrderBackwardsRateConstant(Quantity<ThirdOrderRate> quantity) {
        super(quantity);
    }

}
