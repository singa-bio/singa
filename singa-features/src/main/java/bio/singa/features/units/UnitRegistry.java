package bio.singa.features.units;

import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.quantities.MolarConcentration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.quantity.QuantityDimension.*;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.*;

/**
 * @author cl
 */
public class UnitRegistry {

    /**
     * Standard node distance [L] (100 nm)
     */
    public static final Quantity<Length> DEFAULT_SPACE = Quantities.getQuantity(1.0, MICRO(METRE));

    /**
     * Standard time step size [T] (1 us)
     */
    public static final Quantity<Time> DEFAULT_TIME = Quantities.getQuantity(1.0, MICRO(SECOND));

    /**
     * Standard molar concentration unit [N]
     */
    public static final Quantity<AmountOfSubstance> DEFAULT_AMOUNT_OF_SUBSTANCE = Quantities.getQuantity(1.0, NANO(MOLE));

    public static final Unit<Temperature> DEFAULT_TEMPERATURE_UNIT = KELVIN;
    public static final Unit<Mass> DEFAULT_MASS_UNIT = GRAM;

    public static final Unit<Time> DISPLAY_TIME = SECOND;
    public static final Unit<Volume> DISPLAY_VOLUME = LITRE;
    public static final Unit<AmountOfSubstance> DISPLAY_AMOUNT = MICRO(MOLE);
    public static final Unit<MolarConcentration> DISPLAY_CONCENTRATION = MICRO_MOLE_PER_LITRE;
    public static final Unit<Length> DISPLAY_LENGTH = MICRO(METRE);
    private static UnitRegistry instance = getInstance();
    private Quantity<Length> space;
    private Quantity<Time> time;
    private Map<Dimension, Unit> defaultUnits;
    private Map<Dimension, Unit> displayUnits;

    private UnitRegistry() {
        space = DEFAULT_SPACE;
        time = DEFAULT_TIME;

        defaultUnits = new HashMap<>();
        defaultUnits.put(LENGTH, space.getUnit());
        defaultUnits.put(TIME, time.getUnit());
        defaultUnits.put(AMOUNT_OF_SUBSTANCE, DEFAULT_AMOUNT_OF_SUBSTANCE.getUnit());
        defaultUnits.put(MASS, DEFAULT_MASS_UNIT);
        defaultUnits.put(TEMPERATURE, DEFAULT_TEMPERATURE_UNIT);

        displayUnits = new HashMap<>();
        displayUnits.put(LENGTH, DISPLAY_LENGTH);
        displayUnits.put(TIME, DISPLAY_TIME);
        displayUnits.put(AMOUNT_OF_SUBSTANCE, DISPLAY_AMOUNT);
        displayUnits.put(MASS, DEFAULT_MASS_UNIT);
        displayUnits.put(TEMPERATURE, DEFAULT_TEMPERATURE_UNIT);
        displayUnits.put(LITRE.getDimension(), DISPLAY_VOLUME);
        displayUnits.put(MOLE_PER_LITRE.getDimension(), DISPLAY_CONCENTRATION);
    }

    private static UnitRegistry getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    public static void reinitialize() {
        synchronized (UnitRegistry.class) {
            instance = new UnitRegistry();
        }
    }

    public static Quantity<Length> getSpace() {
        return getInstance().space;
    }

    public static void setSpace(Quantity<Length> space) {
        setSpaceScale(space.getValue().doubleValue());
        setSpaceUnit(space.getUnit());
        FeatureRegistry.scale();
    }

    public static double getSpaceScale() {
        return getInstance().space.getValue().doubleValue();
    }

    public static void setSpaceScale(double scale) {
        getInstance().space = Quantities.getQuantity(scale, getInstance().space.getUnit());
    }

    public static Unit<Length> getSpaceUnit() {
        return getInstance().space.getUnit();
    }

    public static void setSpaceUnit(Unit<Length> unit) {
        // only rescale if unit was updated
        getInstance().space = Quantities.getQuantity(getInstance().space.getValue().doubleValue(), unit);
        getInstance().defaultUnits.put(LENGTH, unit);
        rescaleRegisteredUnits();
    }

    public static void resetSpace() {
        setSpaceScale(DEFAULT_SPACE.getValue().doubleValue());
        setSpaceUnit(DEFAULT_SPACE.getUnit());
    }

    public static Quantity<Time> getTime() {
        return getInstance().time;
    }

    public static void setTime(Quantity<Time> time) {
        double previousTime = getTime().getValue().doubleValue();
        setTimeScale(time.getValue().doubleValue());
        setTimeUnit(time.getUnit());
        double currentTime = getTime().getValue().doubleValue();
        double scalingFactor = currentTime / previousTime;
        FeatureRegistry.scale(scalingFactor);
    }

    public static double getTimeScale() {
        return getInstance().time.getValue().doubleValue();
    }

    public static void setTimeScale(double scale) {
        getInstance().time = Quantities.getQuantity(scale, getInstance().time.getUnit());
    }

    public static Unit<Time> getTimeUnit() {
        return getInstance().time.getUnit();
    }

    public static void setTimeUnit(Unit<Time> unit) {
        if (!getInstance().time.getUnit().equals(unit)) {
            getInstance().time = Quantities.getQuantity(getInstance().time.getValue().doubleValue(), unit);
            getInstance().defaultUnits.put(TIME, unit);
            rescaleRegisteredUnits();
        }
    }

    public static void resetTime() {
        setTimeScale(DEFAULT_TIME.getValue().doubleValue());
        setTimeUnit(DEFAULT_TIME.getUnit());
    }

    public static void setUnit(Unit<?> unit) {
        getInstance().defaultUnits.put(unit.getDimension(), unit);
    }

    public static Unit<MolarConcentration> getConcentrationUnit() {
        return getDefaultUnit(MOLE_PER_LITRE);
    }

    public static Unit<Area> getAreaUnit() {
        return getDefaultUnit(SQUARE_METRE);
    }

    public static Quantity<Area> getArea() {
        return getSpace().multiply(getSpace()).asType(Area.class);
    }

    public static Unit<Volume> getVolumeUnit() {
        return getDefaultUnit(CUBIC_METRE);
    }

    public static Quantity<Volume> getVolume() {
        return getSpace().multiply(getSpace()).multiply(getSpace()).asType(Volume.class);
    }

    public static Quantity<MolarConcentration> concentration(double value) {
        return Quantities.getQuantity(value, getConcentrationUnit());
    }

    public static Quantity<MolarConcentration> concentration(double value, Unit<MolarConcentration> unit) {
        return Quantities.getQuantity(value, unit).to(getConcentrationUnit());
    }

    public static <QuantityType extends Quantity<QuantityType>> Quantity<QuantityType> humanReadable(Quantity<QuantityType> quantity) {
        Dimension dimension = quantity.getUnit().getDimension();
        if (!getInstance().displayUnits.containsKey(dimension)) {
            // not base unit and not registered
            addUnitForDimension(dimension, getInstance().displayUnits);
        }
        return quantity.to(getInstance().displayUnits.get(dimension));
    }

    public static Quantity<MolarConcentration> humanReadable(double concentration) {
        return humanReadable(concentration(concentration));
    }

    public static <QuantityType extends Quantity<QuantityType>> Quantity<QuantityType> scale(Quantity<QuantityType> quantity) {
        Quantity<QuantityType> convert = convert(quantity);
        double value = convert.getValue().doubleValue();
        int spaceExponent = getSpaceExponent(convert.getUnit());
        int timeExponent = getTimeExponent(convert.getUnit());
        if (spaceExponent != 0 || timeExponent != 0) {
            if (spaceExponent > 0 && getSpaceScale() != 1.0) {
                value = value / Math.pow(getSpaceScale(), spaceExponent);
            } else {
                value = value * Math.pow(getSpaceScale(), Math.abs(spaceExponent));
            }

            if (timeExponent > 0 && getSpaceScale() != 1.0) {
                value = value / Math.pow(getTimeScale(), timeExponent);
            } else {
                value = value * Math.pow(getTimeScale(), Math.abs(timeExponent));
            }
            return Quantities.getQuantity(value, convert.getUnit());
        }
        return convert;
    }

    public static <QuantityType extends Quantity<QuantityType>> Quantity<QuantityType> scaleTime(Quantity<QuantityType> quantity) {
        Quantity<QuantityType> convert = convert(quantity);
        double value = convert.getValue().doubleValue();
        int timeExponent = getTimeExponent(convert.getUnit());
        if (timeExponent != 0) {
            if (timeExponent > 0 && getSpaceScale() != 1.0) {
                value = value / Math.pow(getTimeScale(), timeExponent);
            } else {
                value = value * Math.pow(getTimeScale(), Math.abs(timeExponent));
            }
            return Quantities.getQuantity(value, convert.getUnit());
        }
        return convert;
    }

    public static <QuantityType extends Quantity<QuantityType>> Quantity<QuantityType> convert(Quantity<QuantityType> quantity) {
        Dimension dimension = quantity.getUnit().getDimension();
        if (!getInstance().defaultUnits.containsKey(dimension)) {
            // not base unit and not registered
            addUnitForDimension(dimension, getInstance().defaultUnits);
        }
        return quantity.to(getInstance().defaultUnits.get(dimension));
    }

    private static void rescaleRegisteredUnits() {
        for (Dimension next : getInstance().defaultUnits.keySet()) {
            if (next.getBaseDimensions() != null) {
                addUnitForDimension(next, getInstance().defaultUnits);
            }
        }
    }

    public static <UnitType extends Quantity<UnitType>> Unit<UnitType> getDefaultUnit(Unit<UnitType> unit) {
        Dimension dimension = unit.getDimension();
        if (!getInstance().defaultUnits.containsKey(dimension)) {
            // not base unit and not registered
            addUnitForDimension(dimension, getInstance().defaultUnits);
        }
        return getInstance().defaultUnits.get(dimension);
    }

    private static void addUnitForDimension(Dimension dimension, Map<Dimension, Unit> unitMap) {
        Unit unit = ONE;
        for (Map.Entry<? extends Dimension, Integer> entry : dimension.getBaseDimensions().entrySet()) {
            unit = unit.multiply(getPreferredUnit(entry.getKey(), entry.getValue(), unitMap));
        }
        unitMap.put(dimension, unit);
    }

    private static Unit getPreferredUnit(Dimension baseDimension, int exponent, Map<Dimension, Unit> unitMap) {
        if (unitMap.containsKey(baseDimension.pow(Math.abs(exponent)))) {
            // if there is a  defined complex unit (such as concentration)
            if (exponent < 0) {
                Unit unit = unitMap.get(baseDimension.pow(Math.abs(exponent)));
                return ONE.divide(unit);
            } else {
                return unitMap.get(baseDimension.pow(exponent));
            }
        } else if (unitMap.containsKey(baseDimension)) {
            // if time or space use system scale
            return unitMap.get(baseDimension).pow(exponent);
        } else {
            // else use si units
            return Units.getInstance().getUnits(baseDimension).iterator().next().pow(exponent);
        }
    }

    public static int getTimeExponent(Unit<?> unit) {
        return getExponent(unit, SECOND);
    }

    public static int getSpaceExponent(Unit<?> unit) {
        return getExponent(unit, METRE);
    }

    private static int getExponent(Unit<?> testUnit, Unit<?> requiredUnit) {
        // check eventual base units
        Map<? extends Unit<?>, Integer> baseUnits = testUnit.getBaseUnits();
        if (baseUnits == null) {
            if (testUnit.getDimension().equals(requiredUnit.getDimension())) {
                return 1;
            }
            return 0;
        }
        for (Map.Entry<? extends Unit<?>, Integer> entry : baseUnits.entrySet()) {
            Unit<?> unit = entry.getKey();
            if (unit.getDimension().equals(requiredUnit.getDimension())) {
                return entry.getValue();
            }
            if (unit instanceof TransformedUnit) {
                int scale = getExponent(unit, requiredUnit);
                if (scale != 0) {
                    return entry.getValue();
                }
            }
        }
        return 0;
    }

    public static Map<Dimension, Unit> getDefaultUnits() {
        return getInstance().defaultUnits;
    }

    public static boolean isTimeUnit(Unit<?> unit) {
        return unit.isCompatible(SECOND);
    }

    public static boolean isInverseTimeUnit(Unit<?> unit) {
        return unit.isCompatible(ONE.divide(SECOND));
    }

    public static boolean isLengthUnit(Unit<?> unit) {
        return unit.isCompatible(METRE);
    }

    public static boolean isSubstanceUnit(Unit<?> unit) {
        return unit.isCompatible(MOLE);
    }

    public static boolean isConcentrationUnit(Unit<?> unit) {
        return unit.isCompatible(MOLE_PER_LITRE);
    }

}
