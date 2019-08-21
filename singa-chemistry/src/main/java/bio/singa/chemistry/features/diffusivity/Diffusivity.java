package bio.singa.chemistry.features.diffusivity;

import bio.singa.chemistry.features.FeatureProviderRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.NaturalConstants;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.util.List;

import static tech.units.indriya.unit.MetricPrefix.CENTI;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * Diffusivity or diffusion coefficient is a proportionality constant between the molar flux due to molecular diffusion
 * and the gradient in the concentration of the species (or the driving force for diffusion). The higher the diffusivity
 * (of one substance with respect to another), the faster they diffuse into each other.
 *
 * @author cl
 */
public class Diffusivity extends ScalableQuantitativeFeature<Diffusivity> implements Quantity<Diffusivity> {

    public static final Unit<Diffusivity> SQUARE_MICROMETRE_PER_SECOND = new ProductUnit<>(MICRO(METRE).pow(2).divide(SECOND));
    public static final Unit<Diffusivity> SQUARE_CENTIMETRE_PER_SECOND = new ProductUnit<>(CENTI(METRE).pow(2).divide(SECOND));
    public static final Unit<Diffusivity> SQUARE_METRE_PER_SECOND = new ProductUnit<>(METRE.pow(2).divide(SECOND));

    private static final Evidence EINSTEIN1905 = new Evidence(Evidence.SourceType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560.");

    /**
     * Every FeatureProvider that is registered in this method is invoked automatically when the Feature is requested
     * for the first time.
     */
    public static void register() {
        FeatureProviderRegistry.addProviderForFeature(Diffusivity.class, DiffusivityProvider.class);
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
    public static Diffusivity calculate(Quantity<Length> radius) {
        final double upper = NaturalConstants.BOLTZMANN_CONSTANT.getValue().doubleValue() * Environment.getTemperature().getValue().doubleValue();
        final double lower = 6 * Math.PI * Environment.getMacroViscosity().getValue().doubleValue() * radius.to(METRE).getValue().doubleValue();
        return new Diffusivity(Quantities.getQuantity(upper / lower, Diffusivity.SQUARE_METRE_PER_SECOND), EINSTEIN1905);
    }

    public Diffusivity(Quantity<Diffusivity> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public Diffusivity(Quantity<Diffusivity> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public Diffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public void setContent(Quantity<Diffusivity> quantity) {
        featureContent = quantity;
        scale();
    }

    @Override
    public Quantity<Diffusivity> add(Quantity<Diffusivity> augend) {
        return getContent().add(augend);
    }

    @Override
    public Quantity<Diffusivity> subtract(Quantity<Diffusivity> subtrahend) {
        return getContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<Diffusivity> divide(Number divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<Diffusivity> multiply(Number multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getContent().inverse();
    }

    @Override
    public Quantity<Diffusivity> to(Unit<Diffusivity> unit) {
        return getContent().to(unit);
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return getContent().asType(type);
    }

    @Override
    public Number getValue() {
        return getContent().getValue();
    }

    @Override
    public Unit<Diffusivity> getUnit() {
        return getContent().getUnit();
    }

}
