package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import java.util.List;

/**
 * @author cl
 */
public class Ratio extends AbstractQuantitativeFeature<Dimensionless> {

    public Ratio(Quantity<Dimensionless> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public Ratio(Quantity<Dimensionless> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public Ratio(Quantity<Dimensionless> quantity) {
        super(quantity);
    }

    public Ratio(double quantity) {
        super(Quantities.getQuantity(quantity, AbstractUnit.ONE));
    }

}
