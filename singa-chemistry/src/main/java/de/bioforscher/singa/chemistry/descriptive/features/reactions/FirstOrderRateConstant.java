package de.bioforscher.singa.chemistry.descriptive.features.reactions;

import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;

/**
 * @author cl
 */
public abstract class FirstOrderRateConstant extends RateConstant<FirstOrderRate> {

    protected FirstOrderRateConstant(Quantity<FirstOrderRate> firstOrderRateQuantity, FeatureOrigin featureOrigin) {
        super(firstOrderRateQuantity, featureOrigin);
    }


    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<FirstOrderRate> scaledQuantity = getFeatureContent().to(new ProductUnit<>(ONE.divide(time.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = scaledQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

}
