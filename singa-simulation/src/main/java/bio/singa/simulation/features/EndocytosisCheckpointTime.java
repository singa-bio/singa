package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.List;

/**
 * @author cl
 */
public class EndocytosisCheckpointTime extends QuantitativeFeature<Time> {

    public EndocytosisCheckpointTime(Quantity<Time> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public EndocytosisCheckpointTime(Quantity<Time> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public EndocytosisCheckpointTime(Quantity<Time> quantity) {
        super(quantity);
    }

}
