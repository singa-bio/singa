package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class AttachmentDistance extends AbstractFeature<Quantity<Length>> {

    /**
     * Size of the dynein complex fom vesicle surface to microtubule.
     */
    public static final AttachmentDistance DEFAULT_DYNEIN_ATTACHMENT_DISTANCE = new AttachmentDistance(Quantities.getQuantity(61, NANO(METRE)), DefaultFeatureSources.JHA2015);

    private static final String SYMBOL = "d_Attachment";

    public AttachmentDistance(Quantity<Length> length, Evidence featureOrigin) {
        super(length, featureOrigin);
    }

    public AttachmentDistance(double length, Evidence featureOrigin) {
        super(Quantities.getQuantity(length, NANO(METRE)), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
