package bio.singa.features.formatter;

import bio.singa.features.units.UnitProvider;
import bio.singa.features.units.UnitRegistry;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.MetricPrefix.FEMTO;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class TimeFormatter implements QuantityFormatter<Time> {

    private static TimeFormatter instance = getInstance();

    public static TimeFormatter getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    private static void reinitialize() {
        synchronized (TimeFormatter.class) {
            instance = new TimeFormatter();
        }
    }

    private TimeFormatter() {

    }

    @Override
    public String format(Quantity<Time> time) {
        int bestInformativeDigits = Integer.MAX_VALUE;
        Unit<Time> bestUnit = FEMTO(SECOND);
        Unit<Time> nextBestUnit = null;
        for (Unit<Time> timeUnit : UnitProvider.TIME_UNITS) {
            int informativeDigits = time.to(timeUnit).getValue().intValue();
            if (informativeDigits != 0 && informativeDigits < bestInformativeDigits) {
                bestInformativeDigits = informativeDigits;
                nextBestUnit = bestUnit;
                bestUnit = timeUnit;
            }
        }
        Quantity<Time> transformed = time.to(bestUnit);
        double untruncated = transformed.getValue().doubleValue();
        int truncated = transformed.getValue().intValue();
        if (nextBestUnit != null) {
            ComparableQuantity<Time> nextBest = Quantities.getQuantity(untruncated - truncated, bestUnit).to(nextBestUnit);
            if (nextBest.getValue().doubleValue() != 0.0) {
                return truncated + " " + bestUnit + " " + nextBest.getValue().intValue() + " " + nextBestUnit;
            }
        }
        return truncated + " " + bestUnit;
    }

    @Override
    public String format(double defaultQuantity) {
        return format(Quantities.getQuantity(defaultQuantity, UnitRegistry.getDefaultUnit(SECOND)));
    }

    public static String formatTime(Quantity<Time> quantity) {
        return getInstance().format(quantity);
    }

}
