package de.bioforscher.singa.simulation.features.permeability;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import static tec.units.ri.AbstractUnit.ONE;
import static tec.units.ri.unit.Units.HERTZ;

/**
 * @author cl
 */
public class MembraneFlipFlop extends AbstractFeature<Quantity<Frequency>> {

    private Quantity<Frequency> scaledQuantity;

    public MembraneFlipFlop(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public MembraneFlipFlop(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency, HERTZ), featureOrigin);
    }

    public void scale(Quantity<Time> targetTimeScale) {
        // transform to specified unit
        Quantity<Frequency> scaledQuantity = getFeatureContent()
                .to(new ProductUnit<>(ONE.divide(targetTimeScale.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(targetTimeScale.getValue());
    }

    public Quantity<Frequency> getScaledQuantity() {
        return this.scaledQuantity;
    }

}
