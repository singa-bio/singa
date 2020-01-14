package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class MembraneDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    public MembraneDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public MembraneDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MembraneDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
