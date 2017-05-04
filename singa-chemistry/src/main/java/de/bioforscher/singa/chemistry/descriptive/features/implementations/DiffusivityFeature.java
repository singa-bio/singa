package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.FeatureProvider;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.units.quantities.Diffusivity;
import de.bioforscher.singa.units.quantities.DynamicViscosity;
import de.bioforscher.singa.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Temperature;
import java.util.Collections;
import java.util.EnumSet;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureAvailability.ENZYME;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureAvailability.SPECIES;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.DIFFUSIVITY;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static de.bioforscher.singa.units.UnitProvider.*;
import static tec.units.ri.AbstractUnit.ONE;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.KELVIN;

/**
 * @author cl
 */
public class DiffusivityFeature extends FeatureProvider<Diffusivity> {

    private static DiffusivityFeature instance = new DiffusivityFeature();

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    public static final Quantity<Temperature> SYSTEM_TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity
     * of Water at 20 C)
     */
    public static final Quantity<DynamicViscosity> SYSTEM_VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    private static final double molarMassOfWater = 18.0153;

    /**
     * The correlation threshold determines, whether to use Wikle correlation
     * (lighter than the threshold) or Young correlation (heavier than the
     * threshold).
     */
    public static final Quantity<MolarMass> CORRELATION_THRESHOLD = Quantities.getQuantity(10000, GRAM_PER_MOLE);

    /**
     * Diffusion calculation coefficient [dimensionless] (8.34e-8 = 0.0000000834) <br>
     * From: Young, M., Carroad, P., and Bell, R. (1980). Estimation of
     * diffusion coefficients of proteins. Biotechnology and Bioengineering,
     * 22(5):947-955.
     */
    public static final Quantity<Dimensionless> YOUNG_COEFFICIENT = Quantities.getQuantity(8.34e-8, ONE);

    /**
     * Diffusion calculation coefficient [dimensionless] (7.4e-8 = 0.000000074) <br>
     * From: Wilke, C. and Chang, P. (1955). Correlation of diffusion coefficients in dilute solutions. AIChE Journal,
     * 1(2):264-270.
     */
    public static final Quantity<Dimensionless> WILKE_COEFFICIENT = Quantities.getQuantity(7.4e-8,
            ONE);

    /**
     * Association parameter to define the effective molecular weight of the
     * solvent. <br>
     * From: Wilke, C. and Chang, P. (1955). Correlation of diffusion coefficients in dilute solutions. AIChE Journal,
     * 1(2):264-270.
     */
    public static final Quantity<Dimensionless> WILKE_ASSOCIATION_WATER = Quantities.getQuantity(2.26, ONE);

    /**
     * Solute transitional diffusion coefficient. Describes the ratio of D in cells to D in water. <br>
     * From: Kao, H. P., Abney, J. R., and Verkman, A. (1993). Determinants of the translational mobility of a small
     * solute in cell cytoplasm. The Journal of cell biology, 120(1):175-184.
     */
    public static final Quantity<Dimensionless> STDF_CELL_WATER = Quantities.getQuantity(0.27, ONE);

    private DiffusivityFeature() {
        this.setAvailabilities(EnumSet.of(SPECIES, ENZYME));
        this.setRequirements(Collections.singleton(MOLAR_MASS));
    }

    public static DiffusivityFeature getInstance() {
        if (instance == null) {
            synchronized (MolarMassFeature.class) {
                instance = new DiffusivityFeature();
            }
        }
        return instance;
    }

    protected <FeaturableType extends Featureable> Feature<Diffusivity> getFeatureFor(FeaturableType featureable) {
        return new Feature<>(DIFFUSIVITY, estimateDiffusivity(featureable));
    }

    /**
     * Estimates the diffusivity of the species using its features. Always returns cm^2/s.
     *
     * @param featureable
     * @return The diffusivity of the species.
     */
    public <FeaturableType extends Featureable> Quantity<Diffusivity> estimateDiffusivity(FeaturableType featureable) {
        // choose which correlation to take
        Feature molarMass = featureable.getFeature(MOLAR_MASS);
        if (molarMass.getValue() < CORRELATION_THRESHOLD.getValue().doubleValue()) {
            return calculateWilkeCorrelation(molarMass.getValue());
        }
        return calculateYoungCorrelation(molarMass.getValue());
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     * Paper: Young, M., Carroad, P., and Bell, R. (1980). Estimation of
     * diffusion coefficients of proteins. Biotechnology and Bioengineering,
     * 22(5):947-955.
     *
     * @param molarMass The molar mass.
     * @return The diffusivity of the species in cm^2/s.
     */
    private  Quantity<Diffusivity> calculateYoungCorrelation(double molarMass) {
        // D = coefficient * (T/n*M^1/3)
        final double diffusivity = YOUNG_COEFFICIENT.getValue().doubleValue()
                * (SYSTEM_TEMPERATURE.getValue().doubleValue()
                / (SYSTEM_VISCOSITY.getValue().doubleValue() * Math.cbrt(molarMass)));
        return Quantities.getQuantity(diffusivity, SQUARE_CENTIMETER_PER_SECOND);
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     * Paper: Wilke, C. and Chang, P. (1955). Correlation of diffusion
     * coefficients in dilute solutions. AIChE Journal, 1(2):264-270.
     *
     * @param molarMass The molar mass.
     * @return The diffusivity of the species in cm^2/s.
     */
    private <FeaturableType extends Featureable> Quantity<Diffusivity> calculateWilkeCorrelation(double molarMass) {
        // a = coefficient * (x * M(H2O))^0.5 * T
        final double dividend = WILKE_COEFFICIENT.getValue().doubleValue()
                * Math.pow(molarMassOfWater * WILKE_ASSOCIATION_WATER.getValue()
                .doubleValue(), 0.5)
                * SYSTEM_TEMPERATURE.getValue().doubleValue();
        // b = n * M(Sp)^0.6
        final double divisor = SYSTEM_VISCOSITY.getValue().doubleValue()
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
