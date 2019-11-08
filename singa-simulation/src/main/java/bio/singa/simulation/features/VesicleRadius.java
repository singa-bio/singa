package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.util.List;

import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class VesicleRadius extends QuantitativeFeature<Length> {

    public static final VesicleRadius DEFAULT_VESICLE_RADIUS = new VesicleRadius(Quantities.getQuantity(50.0, NANO(METRE)), DefaultFeatureSources.EHRLICH2004);

    public VesicleRadius(Quantity<Length> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public VesicleRadius(Quantity<Length> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public VesicleRadius(Quantity<Length> quantity) {
        super(quantity);
    }

}
