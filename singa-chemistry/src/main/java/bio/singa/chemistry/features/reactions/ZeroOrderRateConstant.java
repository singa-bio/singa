package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public abstract class ZeroOrderRateConstant extends RateConstant<ZeroOrderRate> {

    public ZeroOrderRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, Evidence evidence) {
        super(zeroOrderRateQuantity, evidence);
    }

    public ZeroOrderRateConstant(double value, Unit<ZeroOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    public ZeroOrderRateConstant(Quantity<ZeroOrderRate> quantity) {
        super(quantity);
    }

}
