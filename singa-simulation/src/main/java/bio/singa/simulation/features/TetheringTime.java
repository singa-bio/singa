package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class TetheringTime extends AbstractFeature<Quantity<Time>> {

    /**
     * Average maturation time is 100 seconds.
     */
    public static final TetheringTime DEFAULT_TETHERING_TIME = new TetheringTime(Quantities.getQuantity(18.0, SECOND), DefaultFeatureSources.MERRIFIELD2005);

    private static final String SYMBOL = "t_Tethering";

    public TetheringTime(Quantity<Time> time, FeatureOrigin featureOrigin) {
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
