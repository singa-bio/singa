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

    public QuantityFormatter(DecimalFormat valueFormat, Unit<UnitType> targetUnit) {
        this.valueFormat = valueFormat;
        this.targetUnit = targetUnit;
    }

    public QuantityFormatter(Unit<UnitType> targetUnit) {
        this.valueFormat = DEFAULT_VALUE_FORMAT;
        this.targetUnit = targetUnit;
    }

    public String format(Quantity<UnitType> quantity) {
        return valueFormat.format(quantity.to(targetUnit).getValue().doubleValue()) + " " + targetUnit.toString();
    }

}
