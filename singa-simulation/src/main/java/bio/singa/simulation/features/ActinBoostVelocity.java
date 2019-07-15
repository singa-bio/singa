package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;
import tec.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;
import java.util.List;

import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;
import static tec.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class ActinBoostVelocity extends ScalableQuantitativeFeature<Speed> {

    public static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    public ActinBoostVelocity(Quantity<Speed> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public ActinBoostVelocity(Quantity<Speed> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public ActinBoostVelocity(Quantity<Speed> quantity) {
        super(quantity);
    }

}
