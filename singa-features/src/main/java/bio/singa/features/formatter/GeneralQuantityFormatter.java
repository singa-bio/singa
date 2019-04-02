package bio.singa.features.formatter;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.text.DecimalFormat;

/**
 * @author cl
 */
public class GeneralQuantityFormatter<UnitType extends Quantity<UnitType>> implements QuantityFormatter<UnitType> {

    public static <UnitType  extends Quantity<UnitType>> QuantityFormatter<UnitType> forUnit(Unit<UnitType> targetUnit) {
        return new GeneralQuantityFormatter<>(targetUnit);
    }

    private static final DecimalFormat DEFAULT_VALUE_FORMAT = new DecimalFormat("0.0000");

    private DecimalFormat valueFormat;
    private Unit<UnitType> targetUnit;
    private boolean displayUnit;

    public GeneralQuantityFormatter(DecimalFormat valueFormat, Unit<UnitType> targetUnit, boolean displayUnit) {
        this.valueFormat = valueFormat;
        this.targetUnit = targetUnit;
        this.displayUnit = displayUnit;
    }

    public GeneralQuantityFormatter(Unit<UnitType> targetUnit) {
        this.targetUnit = targetUnit;
        this.displayUnit = false;
    }

    public GeneralQuantityFormatter(Unit<UnitType> targetUnit, boolean displayUnit) {
        this(DEFAULT_VALUE_FORMAT, targetUnit, displayUnit);
    }

    public String format(Quantity<UnitType> quantity) {
        if (valueFormat == null) {
            return String.valueOf(quantity.to(targetUnit).getValue().doubleValue());
        } else {
            return valueFormat.format(quantity.to(targetUnit).getValue().doubleValue()) + (displayUnit ? " " + targetUnit.toString() : "");
        }
    }

    public Unit<UnitType> getTargetUnit() {
        return targetUnit;
    }

}
