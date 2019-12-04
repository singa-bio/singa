package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractQuantitativeFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import java.util.List;

/**
 * Snare pairs required for fusion event to trigger
 * @author cl
 */
public class SNAREFusionPairs extends AbstractQuantitativeFeature<Dimensionless> {

    public SNAREFusionPairs(Quantity<Dimensionless> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public SNAREFusionPairs(Quantity<Dimensionless> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public SNAREFusionPairs(Quantity<Dimensionless> quantity) {
        super(quantity);
    }

}
