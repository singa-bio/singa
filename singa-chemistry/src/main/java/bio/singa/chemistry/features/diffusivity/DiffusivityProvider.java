package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;
import bio.singa.features.quantities.ConcentrationDiffusivity;
import bio.singa.structure.features.molarmass.MolarMass;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static bio.singa.structure.features.molarmass.MolarMass.GRAM_PER_MOLE;
import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public class DiffusivityProvider extends FeatureProvider<ConcentrationDiffusivity> {

    /**
     * Solute transitional diffusion coefficient. Describes the ratio of D in cells to D in water.
     * May be taken as a term to slow diffusion to account for the cytoplasmic density in cells.<br>
     * From: Kao, H. P., Abney, J. R., and Verkman, A. (1993). Determinants of the translational mobility of a small
     * solute in cell cytoplasm. The Journal of cell biology, 120(1):175-184.
     */
    public static final Quantity<Dimensionless> STDF_CELL_WATER = Quantities.getQuantity(0.27, ONE);
    /**
     * The correlation threshold determines, whether to use Wikle correlation (lighter than the threshold) or Young
     * correlation (heavier than the threshold).
     */
    private static final Quantity<MolarMass> CORRELATION_THRESHOLD = Quantities.getQuantity(10000, GRAM_PER_MOLE);
    private final WilkeDiffusivityCorrelation wilkeCorrelation = new WilkeDiffusivityCorrelation();
    private final YoungDiffusivityCorrelation youngCorrelation = new YoungDiffusivityCorrelation();

    public DiffusivityProvider() {
        setProvidedFeature(ConcentrationDiffusivity.class);
        addRequirement(MolarMass.class);
    }

    @Override
    public <FeatureableType extends Featureable> ConcentrationDiffusivity provide(FeatureableType featureable) {
        MolarMass molarMass = featureable.getFeature(MolarMass.class);
        // choose which correlation to take
        if (molarMass.getValue().doubleValue() < CORRELATION_THRESHOLD.getValue().doubleValue()) {
            // use wilke correlation for entities weighting less than 10000 g/mol
            return wilkeCorrelation.predict(featureable);
        } else {
            // else use young correlation
            return youngCorrelation.predict(featureable);
        }
    }
}
