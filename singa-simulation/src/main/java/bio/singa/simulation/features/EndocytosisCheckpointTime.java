package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class EndocytosisCheckpointTime extends AbstractFeature<Quantity<Time>> {

    private static final String SYMBOL = "t_check";

    public EndocytosisCheckpointTime(Quantity<Time> time, Evidence evidence) {
        super(time, evidence);
    }

    public EndocytosisCheckpointTime(double time, Evidence evidence) {
        super(Quantities.getQuantity(time, SECOND), evidence);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }

}
