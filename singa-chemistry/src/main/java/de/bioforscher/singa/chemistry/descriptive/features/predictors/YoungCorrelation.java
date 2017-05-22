package de.bioforscher.singa.chemistry.descriptive.features.predictors;

import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.units.features.diffusivity.Diffusivity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static de.bioforscher.singa.units.UnitProvider.SQUARE_CENTIMETER_PER_SECOND;
import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class YoungCorrelation extends PredictionDescriptor<Diffusivity> {

    /**
     * The instance.
     */
    private static final YoungCorrelation instance = new YoungCorrelation();

    /**
     * Diffusion calculation coefficient [dimensionless] (8.34e-8 = 0.0000000834)
     */
    private static final Quantity<Dimensionless> YOUNG_COEFFICIENT = Quantities.getQuantity(8.34e-8, ONE);

    private YoungCorrelation() {
        this.setSourceName("Young Correlation");
        this.setSourcePublication("Young, M. E., P. A. Carroad, and R. L. Bell. \"Estimation of diffusion coefficients " +
                "of proteins.\" Biotechnology and Bioengineering 22.5 (1980): 947-955.");
    }

    public static YoungCorrelation getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> Quantity<Diffusivity> predict(FeaturableType featureable) {
        return instance.calculate(featureable);
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s.
     *
     * @param featureable The entity to be annotated.
     * @param <FeaturableType> The type of the feature.
     * @return The Diffusivity of the entity in cm^2/s.
     */
    @Override
    public <FeaturableType extends Featureable> Quantity<Diffusivity> calculate(FeaturableType featureable) {
        double molarMass = featureable.getFeature(MOLAR_MASS).getValue();
        // D = coefficient * (T/n*M^1/3)
        final double diffusivity = YOUNG_COEFFICIENT.getValue().doubleValue()
                * (TEMPERATURE.getValue().doubleValue()
                / (VISCOSITY.getValue().doubleValue() * Math.cbrt(molarMass)));
        return Quantities.getQuantity(diffusivity, SQUARE_CENTIMETER_PER_SECOND);
    }

}
