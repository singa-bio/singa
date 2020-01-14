package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class OriginalDiffusivity extends PixelDiffusivity {

    public OriginalDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public OriginalDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public OriginalDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

}
