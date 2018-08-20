package bio.singa.features.model;

import bio.singa.features.units.UnitProvider;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.text.DecimalFormat;

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
     * @param elapsedTime
     * @return
     */
    public static String formatTime(Quantity<Time> elapsedTime) {
        int bestInformativeDigits = Integer.MAX_VALUE;
        Unit<Time> bestUnit = null;
        Unit<Time> nextBestUnit = null;
        for (Unit<Time> timeUnit : UnitProvider.TIME_UNITS) {
            int informativeDigits = elapsedTime.to(timeUnit).getValue().intValue();
            if (informativeDigits != 0 && informativeDigits < bestInformativeDigits) {
                bestInformativeDigits = informativeDigits;
                nextBestUnit = bestUnit;
                bestUnit = timeUnit;
            }
        }
        Quantity<Time> transformed = elapsedTime.to(bestUnit);
        double untruncated = transformed.getValue().doubleValue();
        int truncated = transformed.getValue().intValue();

        ComparableQuantity<Time> nextBest = Quantities.getQuantity(untruncated - truncated, bestUnit).to(nextBestUnit);

        return truncated + " " + bestUnit + " " + nextBest.getValue().intValue() + " " + nextBestUnit;
    }

    public QuantityFormatter(Unit<UnitType> targetUnit, boolean displayUnit) {
        this(DEFAULT_VALUE_FORMAT, targetUnit, displayUnit);
    }

    public String format(Quantity<UnitType> quantity) {
        if (quantity == null) {

        }
        return valueFormat.format(quantity.to(targetUnit).getValue().doubleValue()) + (displayUnit ? " " + targetUnit.toString() : "");
    }

}
