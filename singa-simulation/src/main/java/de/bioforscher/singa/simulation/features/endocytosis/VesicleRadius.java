package de.bioforscher.singa.simulation.features.endocytosis;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class VesicleRadius extends AbstractFeature<Quantity<Length>> {

    public static final String SYMBOL = "r_vesicle";

    public VesicleRadius(Quantity<Length> radius, FeatureOrigin featureOrigin) {
        super(radius, featureOrigin);
    }

    public VesicleRadius(double radius, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(radius, NANO(METRE)), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
