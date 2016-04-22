package de.bioforscher.simulation.diffusion;

import de.bioforscher.units.quantities.Diffusivity;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static de.bioforscher.units.UnitDictionary.SQUARECENTIMETER_PER_SECOND;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

public class DiffusivityRescaling {

    public static void main(String[] args) {

        Quantity<Time> targetTimeScale = Quantities.getQuantity(100.0, NANO(SECOND));
        Quantity<Length> targetLengthScale = Quantities.getQuantity(1.0, NANO(METRE));

        Quantity<Diffusivity> diffusionCoefficient = Quantities.getQuantity(1.66e-5, SQUARECENTIMETER_PER_SECOND);
        Quantity<Diffusivity> scaledQuantity = diffusionCoefficient
                .to(new ProductUnit<Diffusivity>(targetLengthScale.getUnit().pow(2).divide(targetTimeScale.getUnit())));

        scaledQuantity = scaledQuantity.divide(targetLengthScale.getValue()).divide(targetLengthScale.getValue())
                .multiply(targetTimeScale.getValue());

        System.out.println(scaledQuantity);
    }

}
