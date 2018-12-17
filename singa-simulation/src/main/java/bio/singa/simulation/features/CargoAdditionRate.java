package bio.singa.simulation.features;

import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.FirstOrderRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class CargoAdditionRate extends FirstOrderRateConstant implements ForwardsRateConstant<FirstOrderRate> {

    public CargoAdditionRate(Quantity<FirstOrderRate> firstOrderRateQuantity, Evidence evidence) {
        super(firstOrderRateQuantity, evidence);
    }
}
