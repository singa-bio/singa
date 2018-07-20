package bio.singa.simulation.features.scale;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class AppliedScale extends ScalableQuantityFeature<Dimensionless> {

    public static final String SYMBOL = "scale";

    private Quantity<Time> previousTimeStep;

    public AppliedScale(double scale, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(scale, ONE), featureOrigin);
    }

    public AppliedScale() {
        super(Quantities.getQuantity(1.0, ONE), FeatureOrigin.MANUALLY_ANNOTATED);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        if (previousTimeStep != null) {
            // determine how the time step has changed
            double scale = time.to(MILLI(SECOND)).getValue().doubleValue() / previousTimeStep.to(MILLI(SECOND)).getValue().doubleValue();
            // scale to current time step
            scaledQuantity = scaledQuantity.multiply(scale);
            // and half
            halfScaledQuantity = scaledQuantity.multiply(0.5);
        } else {
            scaledQuantity = Quantities.getQuantity(1.0, ONE);
            halfScaledQuantity = Quantities.getQuantity(0.5, ONE);
        }
        previousTimeStep = time;
    }

    @Override
    public Quantity<Dimensionless> getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Quantity<Dimensionless> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
