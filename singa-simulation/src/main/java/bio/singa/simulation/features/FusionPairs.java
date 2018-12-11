package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

/**
 * Snare pairs required for fusion event to trigger
 * @author cl
 */
public class FusionPairs extends AbstractFeature<Quantity<Dimensionless>> {

    private static final String SYMBOL = "i_Pairs";

    public FusionPairs(Quantity<Dimensionless> integer, Evidence evidence) {
        super(integer, evidence);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
