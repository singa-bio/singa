package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.Correlation;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Featureable;
import bio.singa.features.parameters.Environment;
import bio.singa.structure.features.molarmass.MolarMass;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static tec.uom.se.AbstractUnit.ONE;

/**
 * @author cl
 */
public class YoungCorrelation implements Correlation<Diffusivity> {

    private static final Evidence origin = new Evidence(Evidence.OriginType.PREDICTION,
            "Young Correlation",
            "Young, M. E., P. A. Carroad, and R. L. Bell. \"Estimation of diffusion coefficients " +
                    "of proteins.\" Biotechnology and Bioengineering 22.5 (1980): 947-955.");

    /**
     * Diffusion calculation coefficient [dimensionless] (8.34e-8 = 0.0000000834)
     */
    private static final Quantity<Dimensionless> YOUNG_COEFFICIENT = Quantities.getQuantity(8.34e-8, ONE);

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s.
     *
     * @param featureable The entity to be annotated.
     * @param <FeaturableType> The type of the feature.
     * @return The Diffusivity of the entity in cm^2/s.
     */
    @Override
    public <FeaturableType extends Featureable> Diffusivity predict(FeaturableType featureable) {
        final double molarMass = featureable.getFeature(MolarMass.class).getValue().doubleValue();
        // D = coefficient * (T/n*M^1/3)
        final double diffusivity = YOUNG_COEFFICIENT.getValue().doubleValue()
                * (Environment.getTemperature().getValue().doubleValue()
                / (Environment.getMatrixViscosity().getValue().doubleValue() * Math.cbrt(molarMass)));
        final Quantity<Diffusivity> quantity = Quantities.getQuantity(diffusivity, Diffusivity.SQUARE_CENTIMETRE_PER_SECOND);
        return new Diffusivity(quantity, origin);
    }


}
