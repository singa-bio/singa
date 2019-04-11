package bio.singa.simulation.features;

import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cl
 */
public class FeatureRandomizer {

    private static final double DEFAULT_VARIATION = 0.1;

    public static Quantity<Time> varyTime(Quantity<Time> averageTime) {
        // add some random percent of DEFAULT_VARIATION
        double next = averageTime.getValue().doubleValue() + averageTime.getValue().doubleValue() * ThreadLocalRandom.current().nextGaussian() * DEFAULT_VARIATION;
        // calculate next Event
        return Quantities.getQuantity(next, averageTime.getUnit());
    }

    public static Quantity<Length> varyLength(Quantity<Length> averageLength) {
        // add some random percent of DEFAULT_VARIATION
        double next = averageLength.getValue().doubleValue() + averageLength.getValue().doubleValue() * ThreadLocalRandom.current().nextGaussian() * DEFAULT_VARIATION;
        // calculate next Event
        return Quantities.getQuantity(next, averageLength.getUnit());
    }

}
