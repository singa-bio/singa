package bio.singa.chemistry.features.structure3d;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractQuantitativeFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class Radius extends AbstractQuantitativeFeature<Length> {

    public Radius(Quantity<Length> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public Radius(Quantity<Length> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public Radius(Quantity<Length> quantity) {
        super(quantity);
    }
}
