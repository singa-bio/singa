package bio.singa.simulation.features.endocytosis;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.features.DefautFeatureSources;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Length;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class AttachmentDistance extends AbstractFeature<ComparableQuantity<Length>> {

    /**
     * Size of the dynein complex fom vesicle surface to microtubule.
     */
    public static final AttachmentDistance DEFAULT_DYNEIN_ATTACHMENT_DISTANCE = new AttachmentDistance(Quantities.getQuantity(61, NANO(METRE)), DefautFeatureSources.JHA2015);

    private static final String SYMBOL = "d_Attachment";

    public AttachmentDistance(ComparableQuantity<Length> time, FeatureOrigin featureOrigin) {
        super(time, featureOrigin);
    }

    public AttachmentDistance(double length, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(length, NANO(METRE)), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
