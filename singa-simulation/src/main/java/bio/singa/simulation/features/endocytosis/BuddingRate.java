package bio.singa.simulation.features.endocytosis;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.simulation.features.DefautFeatureSources;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class BuddingRate extends ScalableQuantityFeature<SpawnRate> {

    public static final ProductUnit<SpawnRate> PER_SQUARE_NANOMETRE_PER_SECOND = new ProductUnit<>(ONE.divide(NANO(METRE).pow(2).multiply(SECOND)));

    /**
     * About three events per 10^8 nm^2 s^-1, developing into actual vesicles.
     */
    public static final BuddingRate DEFAULT_BUDDING_RATE = new BuddingRate(Quantities.getQuantity(3.0/10.0E8, PER_SQUARE_NANOMETRE_PER_SECOND), DefautFeatureSources.EHRLICH2004);

    public static final String SYMBOL = "k_Budding";

    public BuddingRate(Quantity<SpawnRate> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity.to(PER_SQUARE_NANOMETRE_PER_SECOND), featureOrigin);
    }

    public BuddingRate(double frequency, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(frequency, PER_SQUARE_NANOMETRE_PER_SECOND), featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<SpawnRate> scaledQuantity = getFeatureContent().to(new ProductUnit<>(ONE.divide(NANO(METRE).pow(2).multiply(time.getUnit()))));
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
