package de.bioforscher.singa.chemistry.descriptive.features.diffusivity;

import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.units.features.model.FeatureProvider;
import de.bioforscher.singa.units.features.model.Featureable;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass.GRAM_PER_MOLE;
import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class DiffusivityProvider extends FeatureProvider<Diffusivity> {

    private final WilkeCorrelation wilkeCorrelation = new WilkeCorrelation();
    private final YoungCorrelation youngCorrelation = new YoungCorrelation();

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
    private static final Quantity<Dimensionless> STDF_CELL_WATER = Quantities.getQuantity(0.27, ONE);

    public DiffusivityProvider() {
        setProvidedFeature(Diffusivity.class);
        addRequirement(MolarMass.class);
    }

    @Override
    public <FeatureableType extends Featureable> Diffusivity provide(FeatureableType featureable) {
        MolarMass molarMass = featureable.getFeature(MolarMass.class);
        // choose which correlation to take
        if (molarMass.getValue().doubleValue() < CORRELATION_THRESHOLD.getValue().doubleValue()) {
            // use wilke correlation for entities weighting less than 10000 g/mol
            return this.wilkeCorrelation.predict(featureable);
        } else {
            // else use young correlation
            return this.youngCorrelation.predict(featureable);
        }
    }
}
