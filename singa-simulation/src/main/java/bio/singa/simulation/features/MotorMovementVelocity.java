package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class MotorMovementVelocity extends ScalableQuantityFeature<Speed> {

    private static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    /**
     * Average lateral displacement velocity after scission for 11 seconds.
     */
    public static final MotorMovementVelocity DEFAULT_MOTOR_VELOCITY = new MotorMovementVelocity(Quantities.getQuantity(800.0, NANOMETRE_PER_SECOND), DefaultFeatureSources.EHRLICH2004);

    public static final String SYMBOL = "v_m";

    public MotorMovementVelocity(Quantity<Speed> frequencyQuantity, Evidence evidence) {
        super(frequencyQuantity, evidence);
    }

    public MotorMovementVelocity(double frequency, Evidence evidence) {
        super(Quantities.getQuantity(frequency, NANOMETRE_PER_SECOND), evidence);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
