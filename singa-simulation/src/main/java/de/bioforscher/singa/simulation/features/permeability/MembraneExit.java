package de.bioforscher.singa.simulation.features.permeability;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.Units.HERTZ;

/**
 * @author cl
 */
public class MembraneExit extends ScalableQuantityFeature<Frequency> {

    public static final String SYMBOL = "k_out";

    public MembraneExit(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public MembraneExit(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency, HERTZ), featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<Frequency> scaledQuantity = getFeatureContent().to(new ProductUnit<>(ONE.divide(time.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = scaledQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
