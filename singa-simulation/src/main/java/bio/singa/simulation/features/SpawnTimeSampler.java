package bio.singa.simulation.features;

import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cl
 */
public class SpawnTimeSampler {

    private static final double variation = 0.1;

    public static Quantity<Time> sampleMaturationTime(Quantity<Time> averageTime) {
        // add some random percent of variation
        double next = averageTime.getValue().doubleValue() + averageTime.getValue().doubleValue() * ThreadLocalRandom.current().nextGaussian() * variation;
        // calculate next Event
        return Quantities.getQuantity(next, averageTime.getUnit());
    }

    public static Quantity<Length> sampleVesicleRadius(Quantity<Length> averageRadius) {
        // add some random percent of variation
        double next = averageRadius.getValue().doubleValue() + averageRadius.getValue().doubleValue() * ThreadLocalRandom.current().nextGaussian() * variation;
        // calculate next Event
        return Quantities.getQuantity(next, averageRadius.getUnit());
    }

}
