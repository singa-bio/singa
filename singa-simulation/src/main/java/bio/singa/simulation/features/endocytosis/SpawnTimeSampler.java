package bio.singa.simulation.features.endocytosis;

import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.concurrent.ThreadLocalRandom;

import static tec.uom.se.unit.Units.HERTZ;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class SpawnTimeSampler {

    private static final double variation = 0.1;

    public static Quantity<Time> sampleNextEventTime(Quantity<Time> currentTime, Quantity<Frequency> frequency) {
        // calculate offset
        double next = 1.0 / frequency.to(HERTZ).getValue().doubleValue();
        // add some random percent of variation
        next += next * ThreadLocalRandom.current().nextGaussian() * variation;
        // calculate next Event
        return currentTime.add(Quantities.getQuantity(next, SECOND));
    }

    public static Quantity<Length> sampleNextVesicleRadius(Quantity<Length> averageRadius) {
        // add some random percent of variation
        double next = averageRadius.getValue().doubleValue() + averageRadius.getValue().doubleValue() * ThreadLocalRandom.current().nextGaussian() * variation;
        // calculate next Event
        return Quantities.getQuantity(next, averageRadius.getUnit());
    }

}
