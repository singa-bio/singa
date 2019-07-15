package bio.singa.simulation.features;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class ModifiedDiffusivity extends Diffusivity {

    public ModifiedDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public ModifiedDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public ModifiedDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }
    
}
