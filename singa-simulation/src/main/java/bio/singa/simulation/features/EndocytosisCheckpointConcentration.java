package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class EndocytosisCheckpointConcentration extends QuantitativeFeature<MolarConcentration> {

    public EndocytosisCheckpointConcentration(Quantity<MolarConcentration> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public EndocytosisCheckpointConcentration(Quantity<MolarConcentration> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public EndocytosisCheckpointConcentration(Quantity<MolarConcentration> quantity) {
        super(quantity);
    }

}
