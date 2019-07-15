package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public abstract class FirstOrderRateConstant extends RateConstant<FirstOrderRate> {

    public FirstOrderRateConstant(Quantity<FirstOrderRate> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public FirstOrderRateConstant(Quantity<FirstOrderRate> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public FirstOrderRateConstant(Quantity<FirstOrderRate> quantity) {
        super(quantity);
    }

}
