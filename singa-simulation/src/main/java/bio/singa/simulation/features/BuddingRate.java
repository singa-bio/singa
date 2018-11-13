package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Area;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class BuddingRate extends ScalableQuantityFeature<SpawnRate> {

    public static final ProductUnit<Area> SQUARE_NANOMETRE = new ProductUnit<>(NANO(METRE).pow(2));
    public static final ProductUnit<SpawnRate> PER_SQUARE_NANOMETRE_PER_SECOND = new ProductUnit<>(ONE.divide(SQUARE_NANOMETRE.multiply(SECOND)));

    /**
     * About three events per 10^8 nm^2 s^-1, developing into actual vesicles.
     */
    public static final BuddingRate DEFAULT_BUDDING_RATE = new BuddingRate(Quantities.getQuantity(3.0/10.0E8, PER_SQUARE_NANOMETRE_PER_SECOND), DefaultFeatureSources.EHRLICH2004);

    public static final String SYMBOL = "k_b";

    public BuddingRate(Quantity<SpawnRate> frequencyQuantity, Evidence featureOrigin) {
        super(frequencyQuantity.to(PER_SQUARE_NANOMETRE_PER_SECOND), featureOrigin);
    }

    public BuddingRate(double frequency, Evidence featureOrigin) {
        super(Quantities.getQuantity(frequency, PER_SQUARE_NANOMETRE_PER_SECOND), featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
