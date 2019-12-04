package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;
import java.util.List;

import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class MotorMovementVelocity extends AbstractScalableQuantitativeFeature<Speed> {

    private static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    /**
     * Average lateral displacement velocity after scission for 11 seconds.
     */
    public static final MotorMovementVelocity DEFAULT_MOTOR_VELOCITY = new MotorMovementVelocity(Quantities.getQuantity(800.0, NANOMETRE_PER_SECOND), DefaultFeatureSources.EHRLICH2004);

    public MotorMovementVelocity(Quantity<Speed> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public MotorMovementVelocity(Quantity<Speed> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MotorMovementVelocity(Quantity<Speed> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleTime(getContent()).getValue().doubleValue()* Environment.getSimulationScale();
//        scaledQuantity = UnitRegistry.scaleTime(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
