package bio.singa.features.formatter;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Unit;
import java.text.DecimalFormat;

/**
 * @author cl
 */
public class ConcentrationFormatter extends GeneralQuantityFormatter<MolarConcentration> implements GeneralConcentrationFormatter {

    public static ConcentrationFormatter forUnit(Unit<MolarConcentration> targetUnit) {
        return new ConcentrationFormatter(new DecimalFormat("0.0000E00"), targetUnit, false);
    }

    private ConcentrationFormatter(DecimalFormat valueFormat, Unit<MolarConcentration> targetUnit, boolean displayUnit) {
        super(valueFormat, targetUnit, displayUnit);
    }

    public String format(double quantityValue) {
        return format(UnitRegistry.concentration(quantityValue));
    }

}
