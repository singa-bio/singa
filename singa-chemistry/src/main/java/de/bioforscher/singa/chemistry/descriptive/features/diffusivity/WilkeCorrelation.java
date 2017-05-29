package de.bioforscher.singa.chemistry.descriptive.features.diffusivity;

import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.units.features.model.Correlation;
import de.bioforscher.singa.units.features.model.FeatureOrigin;
import de.bioforscher.singa.units.features.model.Featureable;
import de.bioforscher.singa.units.parameters.EnvironmentalParameters;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static de.bioforscher.singa.units.parameters.EnvironmentalParameterDefaults.MOLAR_MASS_OF_WATER;
import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class WilkeCorrelation implements Correlation<Diffusivity> {

    private static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION,
            "Wilke Correlation",
            "Wilke, C. R., and Pin Chang. \"Correlation of diffusion coefficients in dilute " +
                    "solutions.\" AIChE Journal 1.2 (1955): 264-270.");

    /**
     * Diffusion calculation coefficient [dimensionless] (7.4e-8 = 0.000000074)
     */
    private static final Quantity<Dimensionless> WILKE_COEFFICIENT = Quantities.getQuantity(7.4e-8, ONE);

    /**
     * Association parameter to define the effective molecular weight of the solvent.
     */
    private static final Quantity<Dimensionless> WILKE_ASSOCIATION_WATER = Quantities.getQuantity(2.26, ONE);

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     *
     * @param featureable The entity to be annotated.
     * @param <FeaturableType> The type of the feature.
     * @return The Diffusivity of the entity in cm^2/s.
     */
    @Override
    public <FeaturableType extends Featureable> Diffusivity predict(FeaturableType featureable) {
        final double molarMass = featureable.getFeature(MolarMass.class).getValue().doubleValue();
        // a = coefficient * (x * M(H2O))^0.5 * T
        final double dividend = WILKE_COEFFICIENT.getValue().doubleValue()
                * Math.pow(MOLAR_MASS_OF_WATER * WILKE_ASSOCIATION_WATER.getValue().doubleValue(), 0.5)
                * EnvironmentalParameters.getInstance().getSystemTemperature().getValue().doubleValue();
        // b = n * M(Sp)^0.6
        final double divisor = EnvironmentalParameters.getInstance().getSystemViscosity().getValue().doubleValue()
                * Math.pow(estimateMolarVolume(molarMass), 0.6);
        // D = a / b
        final Quantity<Diffusivity> quantity = Quantities.getQuantity(dividend / divisor, Diffusivity.SQUARE_CENTIMETER_PER_SECOND);
        return new Diffusivity(quantity, origin);
    }

    /**
     * Estimate molar volume from weight.
     *
     * @param molarMass The molar mass.
     * @return The estimated molar volume.
     */
    private static double estimateMolarVolume(double molarMass) {
        // V = 0.968 * M + 13.8
        return 0.968 * molarMass + 13.8;
    }
}
