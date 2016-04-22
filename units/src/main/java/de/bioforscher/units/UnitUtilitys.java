package de.bioforscher.units;

import javax.measure.Unit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitUtilitys {

    public static Pattern divisorPattern = Pattern.compile(".+\\/1(\\d+)");

    public static UnitPrefix getUnitPrefixByPrefix(String prefix) {
        for (UnitPrefix unitPrefix : UnitPrefix.values())
            if (prefix.equals(unitPrefix.getSymbol()))
                return unitPrefix;
        return UnitPrefix.NO_PREFIX;
    }

    public static UnitPrefix getUnitPrefixFromUnit(Unit<?> unit) {
        if (unit.toString().length() > 1)
            return getUnitPrefixByPrefix(unit.toString().substring(0, 1));
        return UnitPrefix.NO_PREFIX;
    }

    public static UnitName getUnitNameBySymbol(String symbol) {
        for (UnitName unitName : UnitName.values())
            if (symbol.equals(unitName.getSymbol()))
                return unitName;
        return null;
    }

    public static UnitName getUnitNameFromUnit(Unit<?> unit) {
        if (unit.toString().length() > 1 && getUnitPrefixFromUnit(unit) != UnitPrefix.NO_PREFIX)
            return getUnitNameBySymbol(unit.toString().substring(1));
        return getUnitNameBySymbol(unit.toString());
    }

    public static UnitPrefix getUnitPrefixFromDivisor(Unit<?> unit) {
        Matcher divisorMatcher = divisorPattern.matcher(unit.toString());
        if (divisorMatcher.find())
            return getUnitPrefixFromScale(divisorMatcher.group(1).length() * -1);
        return UnitPrefix.NO_PREFIX;
    }

    public static UnitPrefix getUnitPrefixFromScale(int exponent) {
        for (UnitPrefix unitPrefix : UnitPrefix.values())
            if (exponent == unitPrefix.getScale())
                return unitPrefix;
        return UnitPrefix.NO_PREFIX;
    }

    public static String formatMultidimensionalUnit(Unit<?> multiDiemnsionalUnit) {
        StringBuilder sb = new StringBuilder();
        sb.append(getUnitPrefixFromDivisor(multiDiemnsionalUnit).getSymbol());
        Map<? extends Unit<?>, Integer> unitsMap = multiDiemnsionalUnit.getProductUnits();
        for (Unit<?> compoundUnit : unitsMap.keySet()) {
            UnitName unitName = UnitUtilitys.getUnitNameFromUnit(compoundUnit);
            sb.append(unitName.getSymbol());
        }
        return sb.toString();
    }

}
