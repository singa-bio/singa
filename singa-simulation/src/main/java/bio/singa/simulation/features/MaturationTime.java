package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import java.util.List;

import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class MaturationTime extends QuantitativeFeature<Time> {

    /**
     * Average maturation time is 100 seconds.
     */
    public static final MaturationTime DEFAULT_MATURATION_TIME = new MaturationTime(Quantities.getQuantity(100.0, SECOND), DefaultFeatureSources.MERRIFIELD2005);

    public MaturationTime(Quantity<Time> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public MaturationTime(Quantity<Time> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MaturationTime(Quantity<Time> quantity) {
        super(quantity);
    }

}
