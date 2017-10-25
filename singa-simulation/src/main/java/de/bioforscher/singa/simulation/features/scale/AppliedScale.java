package de.bioforscher.singa.simulation.features.scale;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableFeature;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.SECOND;

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
            double scale = time.to(MILLI(SECOND)).getValue().doubleValue() / this.previousTimeStep.to(MILLI(SECOND)).getValue().doubleValue();
            // scale to current time step
            this.scaledQuantity = scaledQuantity * scale;
            // and half
            this.halfScaledQuantity = scaledQuantity * 0.5;
        } else {
            this.scaledQuantity = 1.0;
            this.halfScaledQuantity = 0.5;
        }
        this.previousTimeStep = time;
    }

    @Override
    public Double getScaledQuantity() {
        return this.scaledQuantity;
    }

    @Override
    public Double getHalfScaledQuantity() {
        return this.halfScaledQuantity;
    }

}
