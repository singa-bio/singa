package bio.singa.features.model;


import tec.uom.se.unit.TransformedUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Map;

import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public interface ScalableFeature<FeatureContent extends Quantity<FeatureContent>> extends Feature<Quantity<FeatureContent>> {

    void scale();

    Quantity<FeatureContent> getScaledQuantity();

    Quantity<FeatureContent> getHalfScaledQuantity();

    static int getTimeExponent(Unit<?> unit) {
        return getExponent(unit, SECOND);
    }

    static int getSpaceExponent(Unit<?> unit) {
        return getExponent(unit, METRE);
    }

    static int getExponent(Unit<?> testUnit, Unit<?> requiredUnit) {
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

}
