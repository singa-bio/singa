package bio.singa.simulation.features;

import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class AttachmentDistance extends AbstractScalableQuantitativeFeature<Length> {

    public AttachmentDistance(Quantity<Length> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public AttachmentDistance(Quantity<Length> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public AttachmentDistance(Quantity<Length> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleForPixel(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
