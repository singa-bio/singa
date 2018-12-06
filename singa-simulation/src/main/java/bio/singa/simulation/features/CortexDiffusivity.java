package bio.singa.simulation.features;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class CortexDiffusivity extends Diffusivity {

    public CortexDiffusivity(Quantity<Diffusivity> diffusivityQuantity, Evidence origin) {
        super(diffusivityQuantity, origin);
    }

}
