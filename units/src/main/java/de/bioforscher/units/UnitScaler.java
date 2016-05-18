package de.bioforscher.units;

import de.bioforscher.units.quantities.Diffusivity;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.units.ri.unit.Units.ONE;

public class UnitScaler {

    public static Quantity<ReactionRate> rescaleReactionRate(Quantity<ReactionRate> unscaledQuantity,
                                                             Quantity<Time> targetScale) {
        // transform to specified unit
        Quantity<ReactionRate> scaledQuantity = unscaledQuantity
                .to(new ProductUnit<>(ONE.divide(targetScale.getUnit())));
        // transform to specified amount
        scaledQuantity = scaledQuantity.multiply(targetScale.getValue());
        return scaledQuantity;
    }

    public static Quantity<Diffusivity> rescaleDiffusivity(Quantity<Diffusivity> unscaledQuantity,
                                                           Quantity<Time> targetTimeScale, Quantity<Length> targetLengthScale) {
        // transform to specified unit
        Quantity<Diffusivity> scaledQuantity = unscaledQuantity
                .to(new ProductUnit<>(targetLengthScale.getUnit().pow(2).divide(targetTimeScale.getUnit())));
        // transform to specified amount
        scaledQuantity = scaledQuantity.divide(targetLengthScale.getValue()).divide(targetLengthScale.getValue())
                .multiply(targetTimeScale.getValue());
        return scaledQuantity;
    }

}
