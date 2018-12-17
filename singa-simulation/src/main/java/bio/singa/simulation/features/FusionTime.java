package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.List;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class FusionTime extends QuantitativeFeature<Time> {

    public static final FusionTime DEFAULT_FUSION_TIME = new FusionTime(Quantities.getQuantity(18.0, SECOND), DefaultFeatureSources.DONOVAN2015);

    public FusionTime(Quantity<Time> timeQuantity, List<Evidence> evidence) {
        super(timeQuantity, evidence);
    }

    public FusionTime(Quantity<Time> timeQuantity, Evidence evidence) {
        super(timeQuantity, evidence);
    }

    public FusionTime(Quantity<Time> timeQuantity) {
        super(timeQuantity);
    }

}
