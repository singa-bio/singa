package de.bioforscher.singa.chemistry.features.reactions;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public abstract class ZeroOrderRateConstant extends RateConstant<ZeroOrderRate> {

    public ZeroOrderRateConstant(Quantity<ZeroOrderRate> zeroOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(zeroOrderRateQuantity, featureOrigin);
    }

    public ZeroOrderRateConstant(double value, Unit<ZeroOrderRate> unit, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(value, unit), featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<ZeroOrderRate> scaledQuantity = getFeatureContent().to(new ProductUnit<>(Environment.getConcentrationUnit().divide(time.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = scaledQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

}
