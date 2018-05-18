package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.NaturalConstants;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import javax.measure.quantity.Volume;

import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class Vesicle {

    private static final FeatureOrigin EINSTEIN1905 = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560." );

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
    private static Diffusivity calculateDiffusivity(Quantity<Length> radius) {
        final double upper = NaturalConstants.BOLTZMANN_CONSTANT.getValue().doubleValue() * EnvironmentalParameters.getTemperature().getValue().doubleValue();
        final double lower = 6 * Math.PI * EnvironmentalParameters.getViscosity().getValue().doubleValue() * radius.to(METRE).getValue().doubleValue();
        Diffusivity diffusivity = new Diffusivity(Quantities.getQuantity(upper / lower, Diffusivity.SQUARE_METRE_PER_SECOND), EINSTEIN1905);
        diffusivity.scale(EnvironmentalParameters.getTimeStep(), EnvironmentalParameters.getSystemScale());
        return diffusivity;
    }

    /**
     * The volume of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The volume.
     */
    private static Quantity<Volume> calculateVolume(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(radius).multiply(Math.PI).multiply(4.0 / 3.0).asType(Volume.class);
    }

    private Quantity<Length> radius;
    private Quantity<Volume> volume;
    private Diffusivity diffusivity;

    private Vector2D position;
    private Vector2D potentialUpdate;

    public Vesicle(Vector2D position, Quantity<Length> radius) {
        this.position = position;
        setRadius(radius);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Quantity<Length> getRadius() {
        return radius;
    }

    public Vector2D getPotentialUpdate() {
        return potentialUpdate;
    }

    public void setRadius(Quantity<Length> radius) {
        volume = calculateVolume(radius);
        diffusivity = calculateDiffusivity(radius);
        this.radius = radius;
    }

    public void calculateDisplacement(Quantity<Time> timeStep) {
        double scaling = EnvironmentalParameters.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(2.0 *
                diffusivity.getScaledQuantity().getValue().doubleValue() * timeStep.getValue().doubleValue()),
                EnvironmentalParameters.getSystemScale().getUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        potentialUpdate = position.add(gaussian.multiply(scaling));
    }

    public void permitDisplacement() {
        potentialUpdate = position;
    }

    public void move() {
        position = potentialUpdate;
    }

    @Override
    public String toString() {
        return "Vesicle{" +
                "radius=" + radius +
                ", position=" + position +
                '}';
    }
}
