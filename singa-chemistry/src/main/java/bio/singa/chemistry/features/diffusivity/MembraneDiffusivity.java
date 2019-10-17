package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class MembraneDiffusivity extends ScalableQuantitativeFeature<Diffusivity> {

    public MembraneDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public MembraneDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MembraneDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }
}
