package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import java.util.List;

/**
 * @author cl
 */
public class DetachmentProbability extends ScalableQuantitativeFeature<Frequency> {

    public DetachmentProbability(Quantity<Frequency> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public DetachmentProbability(Quantity<Frequency> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public DetachmentProbability(Quantity<Frequency> quantity) {
        super(quantity);
    }

}
