package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractQuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class EndocytosisCheckpointConcentration extends AbstractQuantitativeFeature<MolarConcentration> {

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
