package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.FeatureProvider;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.WilkeCorrelation;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.YoungCorrelation;
import de.bioforscher.singa.units.features.diffusivity.Diffusivity;
import de.bioforscher.singa.units.features.molarmass.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import java.util.Collections;
import java.util.EnumSet;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureAvailability.*;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.DIFFUSIVITY;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static de.bioforscher.singa.units.UnitProvider.GRAM_PER_MOLE;
import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class DiffusivityFeatureProvider extends FeatureProvider {

    private static DiffusivityFeatureProvider instance = new DiffusivityFeatureProvider();

    /**
     * The correlation threshold determines, whether to use Wikle correlation (lighter than the threshold) or Young
     * correlation (heavier than the threshold).
     */
    private static final Quantity<MolarMass> CORRELATION_THRESHOLD = Quantities.getQuantity(10000, GRAM_PER_MOLE);

    /**
     * Solute transitional diffusion coefficient. Describes the ratio of D in cells to D in water.
     * May be taken as a term to slow diffusion to account for the cytoplasmic density in cells.<br>
     * From: Kao, H. P., Abney, J. R., and Verkman, A. (1993). Determinants of the translational mobility of a small
     * solute in cell cytoplasm. The Journal of cell biology, 120(1):175-184.
     */
    public static final Quantity<Dimensionless> STDF_CELL_WATER = Quantities.getQuantity(0.27, ONE);

    private DiffusivityFeatureProvider() {
        setProvidedFeature(DIFFUSIVITY);
        setRequirements(Collections.singleton(MOLAR_MASS));
        setAvailabilities(EnumSet.of(SPECIES, PROTEIN, ENZYME));
    }

    public static DiffusivityFeatureProvider getInstance() {
        if (instance == null) {
            synchronized (MolarMassFeatureProvider.class) {
                instance = new DiffusivityFeatureProvider();
            }
        }
        return instance;
    }

    /**
     * Estimates the diffusivity of the species using its features. Always returns cm^2/s.
     *
     * @param featureable      The entity to be annotated.
     * @param <FeaturableType> The type of the feature.
     * @return The Diffusivity of the entity in cm^2/s.
     */
    protected <FeaturableType extends Featureable> Feature<Diffusivity> getFeatureFor(FeaturableType featureable) {
        Feature molarMass = featureable.getFeature(MOLAR_MASS);
        Quantity<Diffusivity> diffusivity;
        Feature<Diffusivity> feature = new Feature<>(DIFFUSIVITY);
        // choose which correlation to take
        if (molarMass.getValue() < CORRELATION_THRESHOLD.getValue().doubleValue()) {
            // use wilke correlation for entities weighting less than 10000 g/mol
            feature.setQuantity(WilkeCorrelation.predict(featureable));
            feature.setDescriptor(WilkeCorrelation.getInstance());
        } else {
            // else use young correlation
            feature.setQuantity(YoungCorrelation.predict(featureable));
            feature.setDescriptor(YoungCorrelation.getInstance());
        }
        return feature;
    }

}
