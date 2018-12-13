package bio.singa.simulation.features;

import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.FirstOrderRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class CargoAdditionRate extends FirstOrderRateConstant implements ForwardsRateConstant<FirstOrderRate> {

    public static final String SYMBOL = "k_add";

    public CargoAdditionRate(Quantity<FirstOrderRate> firstOrderRateQuantity, Evidence evidence) {
        super(firstOrderRateQuantity, evidence);
    }

    public CargoAdditionRate(double value, Unit<FirstOrderRate> unit, Evidence evidence) {
        super(Quantities.getQuantity(value, unit), evidence);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }

}
