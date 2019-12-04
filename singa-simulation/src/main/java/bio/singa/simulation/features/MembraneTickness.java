package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractQuantitativeFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class MembraneTickness extends AbstractQuantitativeFeature<Length> {

    public MembraneTickness(Quantity<Length> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public MembraneTickness(Quantity<Length> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MembraneTickness(Quantity<Length> quantity) {
        super(quantity);
    }

}
