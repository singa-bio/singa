package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class EndocytosisCkeckpointConcentration extends AbstractFeature<Quantity<MolarConcentration>> {

    private static final String SYMBOL = "cMax";

    public EndocytosisCkeckpointConcentration(Quantity<MolarConcentration> concentration, Evidence evidence) {
        super(concentration, evidence);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
