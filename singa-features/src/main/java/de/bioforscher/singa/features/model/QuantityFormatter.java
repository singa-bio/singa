package de.bioforscher.singa.features.model;

import javax.measure.Quantity;
import javax.measure.Unit;
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

    public QuantityFormatter(Unit<UnitType> targetUnit, boolean displayUnit) {
        this(DEFAULT_VALUE_FORMAT, targetUnit, displayUnit);
    }

    public String format(Quantity<UnitType> quantity) {
        if(quantity == null) {

        }
        return valueFormat.format(quantity.to(targetUnit).getValue().doubleValue()) + (displayUnit ? " " + targetUnit.toString() : "");
    }

}
