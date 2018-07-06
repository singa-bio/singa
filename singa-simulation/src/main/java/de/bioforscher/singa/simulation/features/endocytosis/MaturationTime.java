package de.bioforscher.singa.simulation.features.endocytosis;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class MaturationTime extends AbstractFeature<ComparableQuantity<Time>> {

    public static final String SYMBOL = "r_vesicle";

    public MaturationTime(ComparableQuantity<Time> time, FeatureOrigin featureOrigin) {
        super(time, featureOrigin);
    }

    public MaturationTime(double time, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(time, SECOND), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
