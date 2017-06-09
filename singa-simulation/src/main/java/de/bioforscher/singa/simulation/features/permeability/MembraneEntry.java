package de.bioforscher.singa.simulation.features.permeability;

import de.bioforscher.singa.units.features.model.AbstractFeature;
import de.bioforscher.singa.units.features.model.FeatureOrigin;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

import static tec.units.ri.unit.Units.HERTZ;

/**
 * @author cl
 */
public class MembraneEntry extends AbstractFeature<Quantity<Frequency>> {

    public MembraneEntry(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public MembraneEntry(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency, HERTZ), featureOrigin);
    }

}
