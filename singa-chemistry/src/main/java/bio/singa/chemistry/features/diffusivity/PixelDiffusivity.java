package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.NaturalConstants;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class PixelDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    private static final Evidence EINSTEIN1905 = new Evidence(Evidence.SourceType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560.");

    private static Map<Quantity<Length>, PixelDiffusivity> cache;

    private static Map<Quantity<Length>, PixelDiffusivity> getCache() {
        if (cache == null) {
            synchronized (FeatureRegistry.class) {
                cache = new HashMap<>();
            }
        }
        return cache;
    }

    /**
     * The diffusivity can be calculated according to the Stokes–Einstein equation:
     * D = (k_B * T) / (6 * pi * nu * radius)
     * k_B is the {@link NaturalConstants#BOLTZMANN_CONSTANT} (in (N * m) / K),
     * T is the Temperature (in K),
     * nu is the dynamic viscosity (in (N * s) / m^2 ) and,
     *
     * @param radius the radius of the vesicle
     * @return The diffusivity.
     */
    public static PixelDiffusivity  calculate(Quantity<Length> radius) {
        PixelDiffusivity cachedDiffusivity = getCache().get(radius);
        if (cachedDiffusivity != null) {
            return cachedDiffusivity;
        }
        final double upper = NaturalConstants.BOLTZMANN_CONSTANT.getValue().doubleValue() * Environment.getTemperature().getValue().doubleValue();
        final double lower = 6 * Math.PI * Environment.getMacroViscosity().getValue().doubleValue() * radius.to(METRE).getValue().doubleValue();
        Quantity<Diffusivity> diffusivity = Quantities.getQuantity(upper / lower, Diffusivity.SQUARE_METRE_PER_SECOND).asType(Diffusivity.class);
        PixelDiffusivity pixelDiffusivity = new PixelDiffusivity(diffusivity, EINSTEIN1905);
        getCache().put(radius, pixelDiffusivity);
        return pixelDiffusivity;
    }

    public PixelDiffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public PixelDiffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public PixelDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    public void setContent(Quantity<Diffusivity> quantity) {
        featureContent = quantity;
        scale();
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleForPixel(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
