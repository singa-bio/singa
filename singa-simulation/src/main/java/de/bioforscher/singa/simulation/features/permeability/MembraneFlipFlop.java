package de.bioforscher.singa.simulation.features.permeability;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

import static tec.units.ri.unit.Units.HERTZ;

/**
 * @author cl
 */
public class MembraneFlipFlop extends AbstractFeature<Quantity<Frequency>> {

    public MembraneFlipFlop(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public MembraneFlipFlop(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency, HERTZ), featureOrigin);
    }

}
