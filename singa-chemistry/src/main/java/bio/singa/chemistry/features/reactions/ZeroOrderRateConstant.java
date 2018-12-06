package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public abstract class ZeroOrderRateConstant extends RateConstant<ZeroOrderRate> {

    public ZeroOrderRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, Evidence featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderRateConstant(double value, Unit<ZeroOrderRate> unit, Evidence featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

}
