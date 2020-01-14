package bio.singa.simulation.features;

import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class AttachmentDistance extends AbstractScalableQuantitativeFeature<Length> {

    /**
     * Size of the dynein complex fom vesicle surface to microtubule.
     */
    public static final AttachmentDistance DEFAULT_DYNEIN_ATTACHMENT_DISTANCE = new AttachmentDistance(Quantities.getQuantity(61, NANO(METRE)), DefaultFeatureSources.JHA2015);

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
