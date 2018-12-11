package bio.singa.features.model;

import bio.singa.features.units.UnitProvider;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.text.DecimalFormat;

import static tec.uom.se.unit.MetricPrefix.FEMTO;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class QuantityFormatter<UnitType extends Quantity<UnitType>> {

    private static final DecimalFormat DEFAULT_VALUE_FORMAT = new DecimalFormat("0.0000");

    private DecimalFormat valueFormat;
    private Unit<UnitType> targetUnit;
    private boolean displayUnit;

    public QuantityFormatter(DecimalFormat valueFormat, Unit<UnitType> targetUnit, boolean displayUnit) {
        this.valueFormat = valueFormat;
        this.targetUnit = targetUnit;
        this.displayUnit = displayUnit;
    }

    /**
     * Formats the time to the shortest informative representation.
     *
     * @param time The time to be formatted.
     * @return
     */
    public static String formatTime(Quantity<Time> time) {
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

    public QuantityFormatter(Unit<UnitType> targetUnit, boolean displayUnit) {
        this(DEFAULT_VALUE_FORMAT, targetUnit, displayUnit);
    }

    public String format(Quantity<UnitType> quantity) {
        return valueFormat.format(quantity.to(targetUnit).getValue().doubleValue()) + (displayUnit ? " " + targetUnit.toString() : "");
    }

    public Unit<UnitType> getTargetUnit() {
        return targetUnit;
    }

    public String format(double quantityValue) {
        return valueFormat.format(quantityValue) + (displayUnit ? " " + targetUnit.toString() : "");
    }

}
