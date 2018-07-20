package de.bioforscher.singa.simulation.features.endocytosis;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableQuantityFeature;
import de.bioforscher.singa.simulation.features.DefautFeatureSources;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ActinBoostVelocity extends ScalableQuantityFeature<Speed> {

    public static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    /**
     * Average lateral displacement velocity after scission for 11 seconds.
     */
    public static final ActinBoostVelocity DEFAULT_ACTIN_VELOCITY = new ActinBoostVelocity(Quantities.getQuantity(57.0, NANOMETRE_PER_SECOND), DefautFeatureSources.EHRLICH2004);

    public static final String SYMBOL = "v_ActinBoost";

    public ActinBoostVelocity(Quantity<Speed> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public ActinBoostVelocity(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency,NANOMETRE_PER_SECOND ), featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<Speed> scaledQuantity = getFeatureContent().to(new ProductUnit<>(space.getUnit().divide(time.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = scaledQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
