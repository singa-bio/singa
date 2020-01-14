package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class ConcentrationDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    private static Map<Quantity<Length>, Diffusivity> cache;

    private static Map<Quantity<Length>, Diffusivity> getCache() {
        if (cache == null) {
            synchronized (FeatureRegistry.class) {
                cache = new HashMap<>();
            }
        }
        return cache;
    }

    public ConcentrationDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public ConcentrationDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public ConcentrationDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
