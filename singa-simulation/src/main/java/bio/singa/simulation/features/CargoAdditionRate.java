package bio.singa.simulation.features;

import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.FirstOrderRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class CargoAdditionRate extends FirstOrderRateConstant implements ForwardsRateConstant<FirstOrderRate> {

    public CargoAdditionRate(Quantity<FirstOrderRate> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public CargoAdditionRate(Quantity<FirstOrderRate> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public CargoAdditionRate(Quantity<FirstOrderRate> quantity) {
        super(quantity);
    }

}
