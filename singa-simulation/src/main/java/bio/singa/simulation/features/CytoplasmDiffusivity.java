package bio.singa.simulation.features;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class CytoplasmDiffusivity extends Diffusivity {

    public CytoplasmDiffusivity(Quantity<Diffusivity> diffusivityQuantity, FeatureOrigin origin) {
        super(diffusivityQuantity, origin);
    }

}
