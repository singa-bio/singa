package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class FirstOrderBackwardsRateConstant extends FirstOrderRateConstant implements BackwardsRateConstant<FirstOrderRate> {

    public FirstOrderBackwardsRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, Evidence evidence) {
        super(firstOrderRateQuantity, evidence);
    }

    public FirstOrderBackwardsRateConstant(double value, Unit<FirstOrderRate> firstOrderRateQuantity, Evidence evidence) {
        super(Quantities.getQuantity(value, firstOrderRateQuantity), evidence);
    }

    public FirstOrderBackwardsRateConstant(Quantity<FirstOrderRate> quantity) {
        super(quantity);
    }

}
