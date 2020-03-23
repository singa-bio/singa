package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.NaturalConstants;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.util.HashMap;
import java.util.Map;

import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class PixelDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    private static final Evidence EINSTEIN1905 = new Evidence(Evidence.SourceType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560.");

    private static Map<Quantity<Length>, PixelDiffusivity> cache;

    public PixelDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

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
    public static PixelDiffusivity calculate(Quantity<Length> radius) {
        PixelDiffusivity cachedDiffusivity = getCache().get(radius);
        if (cachedDiffusivity != null) {
            return cachedDiffusivity;
        }
        final double upper = NaturalConstants.BOLTZMANN_CONSTANT.getValue().doubleValue() * Environment.getTemperature().getValue().doubleValue();
        final double lower = 6 * Math.PI * Environment.getMacroViscosity().getValue().doubleValue() * radius.to(METRE).getValue().doubleValue();
        Quantity<Diffusivity> diffusivity = Quantities.getQuantity(upper / lower, Diffusivity.SQUARE_METRE_PER_SECOND).asType(Diffusivity.class);
        PixelDiffusivity pixelDiffusivity = PixelDiffusivity.of(diffusivity)
                .comment("diffusivity of macroscopic entities")
                .evidence(EINSTEIN1905)
                .build();
        getCache().put(radius, pixelDiffusivity);
        return pixelDiffusivity;
    }

    public static Builder of(Quantity<Diffusivity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Diffusivity> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
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

    public static class Builder extends AbstractFeature.Builder<Quantity<Diffusivity>, PixelDiffusivity, Builder> {

        public Builder(Quantity<Diffusivity> quantity) {
            super(quantity);
        }

        @Override
        protected PixelDiffusivity createObject(Quantity<Diffusivity> quantity) {
            return new PixelDiffusivity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
