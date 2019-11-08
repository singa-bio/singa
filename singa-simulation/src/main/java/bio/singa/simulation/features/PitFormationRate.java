package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import java.util.List;

import static bio.singa.simulation.features.SpawnRate.PER_SQUARE_NANOMETRE_PER_SECOND;

/**
 * @author cl
 */
public class PitFormationRate extends ScalableQuantitativeFeature<SpawnRate> {

    /**
     * About three events per 10^8 nm^2 s^-1, developing into actual vesicles.
     */
    public static final PitFormationRate DEFAULT_BUDDING_RATE = new PitFormationRate(Quantities.getQuantity(3.0/10.0E8, PER_SQUARE_NANOMETRE_PER_SECOND), DefaultFeatureSources.EHRLICH2004);

    public PitFormationRate(Quantity<SpawnRate> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public PitFormationRate(Quantity<SpawnRate> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public PitFormationRate(Quantity<SpawnRate> quantity) {
        super(quantity);
    }

}
