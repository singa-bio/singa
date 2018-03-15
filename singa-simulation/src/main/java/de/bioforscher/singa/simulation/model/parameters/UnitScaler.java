package de.bioforscher.singa.simulation.model.parameters;


import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;

public class UnitScaler {

    /**
     * Scales the reaction rate for the use with cellular graph automata. The unscaled quantity is transformed to the
     * unit specified by target time and multiplied by the value of the time scale.
     *
     * @param unscaledQuantity The quantity that is to be scaled.
     * @param targetScale The required time step.
     * @return The scaled reaction rate.
     */
    public static Quantity<Frequency> rescaleReactionRate(Quantity<Frequency> unscaledQuantity,
                                                          Quantity<Time> targetScale) {
        // transform to specified unit
        Quantity<Frequency> scaledQuantity = unscaledQuantity
                .to(new ProductUnit<>(ONE.divide(targetScale.getUnit())));
        // transform to specified amount
        scaledQuantity = scaledQuantity.multiply(targetScale.getValue());
        return scaledQuantity;
    }

    /**
     * Scales the diffusivity for the use with cellular graph automata. The unscaled quantity is transformed to the
     * unit specified by target time and length scales. Further the unscaled quantity is divided by the squared length
     * scale and multiplied by the time scale.
     *
     * @param unscaledQuantity The quantity that is to be scaled.
     * @param targetTimeScale The required time step.
     * @param targetLengthScale The required spatial step.
     * @return The scaled diffusivity.
     */
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
