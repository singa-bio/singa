package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import tec.uom.se.AbstractUnit;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import java.util.List;

/**
 * @author cl
 */
public class Ratio extends QuantitativeFeature<Dimensionless> {

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
