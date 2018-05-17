package de.bioforscher.singa.chemistry.descriptive.features.reactions;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.Units.HERTZ;
import static tec.uom.se.unit.Units.MINUTE;

/**
 * The turnover number is the maximal number of substrate molecules converted to product by enzyme and second.
 *
 * @author cl
 */
public class TurnoverNumber extends ScalableQuantityFeature<Frequency> {

    public static final Unit<Frequency> PER_SECOND = HERTZ;
    public static final Unit<Frequency> PER_MINUTE = new ProductUnit<>(ONE.divide(MINUTE));
    public static final String SYMBOL = "k_cat";

    private Quantity<Frequency> scaledQuantity;
    private Quantity<Frequency> halfScaledQuantity;

    public TurnoverNumber(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<Frequency> scaledQuantity = getFeatureContent()
                .to(new ProductUnit<>(ONE.divide(time.getUnit())));
        // transform to specified amount
        this.scaledQuantity = scaledQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = scaledQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

    @Override
    public Quantity<Frequency> getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Quantity<Frequency> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
