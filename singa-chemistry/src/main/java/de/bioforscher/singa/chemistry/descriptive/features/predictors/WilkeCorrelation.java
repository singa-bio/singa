package de.bioforscher.singa.chemistry.descriptive.features.predictors;

import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.units.quantities.Diffusivity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static de.bioforscher.singa.units.UnitProvider.SQUARE_CENTIMETER_PER_SECOND;
import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class WilkeCorrelation extends PredictionDescriptor<Diffusivity> {

    /**
     * The instance.
     */
    private static final WilkeCorrelation instance = new WilkeCorrelation();

    /**
     * Diffusion calculation coefficient [dimensionless] (7.4e-8 = 0.000000074)
     */
    private static final Quantity<Dimensionless> WILKE_COEFFICIENT = Quantities.getQuantity(7.4e-8, ONE);

    /**
     * Association parameter to define the effective molecular weight of the solvent.
     */
    private static final Quantity<Dimensionless> WILKE_ASSOCIATION_WATER = Quantities.getQuantity(2.26, ONE);

    /**
     * The molar mass of water.
     */
    private static final double MOLAR_MASS_OF_WATER = 18.0153;

    private WilkeCorrelation() {
        setMethodName("Wilke Correlation");
        setMethodPublication("Wilke, C. R., and Pin Chang. \"Correlation of diffusion coefficients in dilute " +
                "solutions.\" AIChE Journal 1.2 (1955): 264-270.");
    }

    public static WilkeCorrelation getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> Quantity<Diffusivity> predict(FeaturableType featureable) {
        return instance.calculate(featureable);
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     *
     * @param featureable The entity to be annotated.
     * @param <FeaturableType> The type of the feature.
     * @return The Diffusivity of the entity in cm^2/s.
     */
    @Override
    public <FeaturableType extends Featureable> Quantity<Diffusivity> calculate(FeaturableType featureable) {
        final double molarMass = featureable.getFeature(MOLAR_MASS).getValue();
        // a = coefficient * (x * M(H2O))^0.5 * T
        final double dividend = WILKE_COEFFICIENT.getValue().doubleValue()
                * Math.pow(MOLAR_MASS_OF_WATER * WILKE_ASSOCIATION_WATER.getValue()
                .doubleValue(), 0.5)
                * TEMPERATURE.getValue().doubleValue();
        // b = n * M(Sp)^0.6
        final double divisor = VISCOSITY.getValue().doubleValue()
                * Math.pow(estimateMolarVolume(molarMass), 0.6);
        // D = a / b
        return Quantities.getQuantity(dividend / divisor, SQUARE_CENTIMETER_PER_SECOND);
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
