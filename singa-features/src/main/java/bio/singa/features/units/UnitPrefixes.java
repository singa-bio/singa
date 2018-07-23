package bio.singa.features.units;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides methods to handle unit prefixes, scales and names.
 */
public class UnitPrefixes {

    /**
     * recognizes if a string contains a divisor
     */
    private static final Pattern divisorPattern = Pattern.compile(".+/1(\\d+)");

    /**
     * Gets the {@link UnitPrefix} from the {@code String} representation of a prefix.
     *
     * @param prefix The prefix as string.
     * @return The {@link UnitPrefix}.
     */
    public static UnitPrefix getUnitPrefixByPrefix(String prefix) {
        for (UnitPrefix unitPrefix : UnitPrefix.values())
            if (prefix.equals(unitPrefix.getSymbol()))
                return unitPrefix;
        return UnitPrefix.NO_PREFIX;
    }

    /**
     * Extracts the {@link UnitPrefix} from an unit.
     *
     * @param unit The unit.
     * @return The prefix from the given unit.
     */
    public static UnitPrefix getUnitPrefixFromUnit(Unit<?> unit) {
        if (unit.toString().length() > 1)
            return getUnitPrefixByPrefix(unit.toString().substring(0, 1));
        return UnitPrefix.NO_PREFIX;
    }

    /**
     * Gets the {@link UnitName} from the {@code String} representation of an unit without prefix.
     *
     * @param symbol The unit as string.
     * @return The {@link UnitName} .
     */
    public static UnitName getUnitNameBySymbol(String symbol) {
        for (UnitName unitName : UnitName.values())
            if (symbol.equals(unitName.getSymbol()))
                return unitName;
        return null;
    }

    /**
     * Extracts the {@link UnitName} from an unit.
     *
     * @param unit The unit.
     * @return The {@link UnitName}.
     */
    public static UnitName getUnitNameFromUnit(Unit<?> unit) {
        if (unit.toString().length() > 1 && getUnitPrefixFromUnit(unit) != UnitPrefix.NO_PREFIX)
            return getUnitNameBySymbol(unit.toString().substring(1));
        return getUnitNameBySymbol(unit.toString());
    }

    /**
     * Gets the {@link UnitPrefix} from the divisor of a product unit.
     *
     * @param unit The product unit.
     * @return The {@link UnitPrefix}.
     */
    public static UnitPrefix getUnitPrefixFromDivisor(Unit<?> unit) {
        Matcher divisorMatcher = divisorPattern.matcher(unit.toString());
        if (divisorMatcher.find())
            return getUnitPrefixFromScale(divisorMatcher.group(1).length() * -1);
        return UnitPrefix.NO_PREFIX;
    }

    /**
     * Gets the {@link UnitPrefix} from the exponent of an unit.
     *
     * @param exponent The exponent.
     * @return The {@link UnitPrefix}.
     */
    public static UnitPrefix getUnitPrefixFromScale(int exponent) {
        for (UnitPrefix unitPrefix : UnitPrefix.values())
            if (exponent == unitPrefix.getScale())
                return unitPrefix;
        return UnitPrefix.NO_PREFIX;
    }

    /**
     * Formats a multidimensional unit to a descriptor string.
     *
     * @param multiDimensionalUnit The multidimensional unit.
     * @return The descriptive string for the given unit.
     */
    public static String formatMultidimensionalUnit(Unit<?> multiDimensionalUnit) {
        StringBuilder sb = new StringBuilder();
        sb.append(getUnitPrefixFromDivisor(multiDimensionalUnit).getSymbol());
        Map<? extends Unit<?>, Integer> unitsMap = multiDimensionalUnit.getBaseUnits();
        for (Unit<?> compoundUnit : unitsMap.keySet()) {
            UnitName unitName = UnitPrefixes.getUnitNameFromUnit(compoundUnit);
            sb.append(unitName.getSymbol());
        }
        return sb.toString();
    }

    /**
     * Generates a list of strings that contains all the given prefixes combined with the {@link UnitName}. Sorted by
     * exponent of the prefix ascending.
     *
     * @param prefixes A set of prefixes.
     * @param unitName {@link UnitName}
     * @return A list of units with prefixes as string
     */
    public static List<String> generateUnitNamesForPrefixes(EnumSet<UnitPrefix> prefixes, UnitName unitName) {
        return prefixes.stream()
                .sorted(Comparator.comparingInt(UnitPrefix::getScale))
                .map(prefix -> prefix.getSymbol() + unitName.getSymbol())
                .collect(Collectors.toList());
    }

    /**
     * Generates a list of units for the all supplied prefixes in combination with the supplied unit.
     *
     * @param prefixes A set of prefixes.
     * @param unit The unit to be modified.
     * @param <Q> The resulting unit type.
     * @return a list of units for the all supplied prefixes.
     */
    public static <Q extends Quantity<Q>> List<Unit<Q>> generateUnitsForPrefixes(EnumSet<UnitPrefix> prefixes, Unit<Q>
            unit) {
        return prefixes.stream()
                .sorted(Comparator.comparingInt(UnitPrefix::getScale))
                .map(p -> unit.transform(p.getCorrespondingConverter()))
                .collect(Collectors.toList());
    }

}
