package de.bioforscher.singa.simulation.features.scale;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class AppliedScale extends AbstractFeature<Double> implements ScalableFeature<Double> {

    private Double scaledQuantity;
    private Double halfScaledQuantity;

    private Quantity<Time> previousTimeStep;

    public AppliedScale(Double scale, FeatureOrigin featureOrigin) {
        super(scale, featureOrigin);
    }

    public AppliedScale() {
        super(1.0, FeatureOrigin.MANUALLY_ANNOTATED);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        if (previousTimeStep != null) {
            // determine how the time step has changed
            double scale = time.to(MILLI(SECOND)).getValue().doubleValue() / previousTimeStep.to(MILLI(SECOND)).getValue().doubleValue();
            // scale to current time step
            scaledQuantity = scaledQuantity * scale;
            // and half
            halfScaledQuantity = scaledQuantity * 0.5;
        } else {
            scaledQuantity = 1.0;
            halfScaledQuantity = 0.5;
        }
        previousTimeStep = time;
    }

    @Override
    public Double getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Double getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

}
