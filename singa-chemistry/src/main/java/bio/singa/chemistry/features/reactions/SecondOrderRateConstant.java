package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public abstract class SecondOrderRateConstant extends RateConstant<SecondOrderRate> {

    public SecondOrderRateConstant(Quantity<SecondOrderRate> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public SecondOrderRateConstant(Quantity<SecondOrderRate> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public SecondOrderRateConstant(Quantity<SecondOrderRate> quantity) {
        super(quantity);
    }

}
