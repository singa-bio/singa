package de.bioforscher.singa.simulation.features.endocytosis;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.simulation.features.DefautFeatureSources;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class TetheringTime extends AbstractFeature<ComparableQuantity<Time>> {

    /**
     * Average maturation time is 100 seconds.
     */
    public static final TetheringTime DEFAULT_TETHERING_TIME = new TetheringTime(Quantities.getQuantity(18.0, SECOND), DefautFeatureSources.MERRIFIELD2005);

    private static final String SYMBOL = "t_Tethering";

    public TetheringTime(ComparableQuantity<Time> time, FeatureOrigin featureOrigin) {
        super(time, featureOrigin);
    }

    public TetheringTime(double time, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(time, SECOND), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
