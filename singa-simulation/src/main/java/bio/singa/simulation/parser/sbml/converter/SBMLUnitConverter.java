package bio.singa.simulation.parser.sbml.converter;

import bio.singa.features.units.UnitPrefixes;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.UnitDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Unit;
import java.util.HashMap;
import java.util.Map;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.Units.*;

/**
 * Converts JSBML Units to UnitsOfMeasurement Units.
 *
 * @author cl
 */
public class SBMLUnitConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLUnitConverter.class);

    public SBMLUnitConverter() {
    }

    public Map<String, Unit<?>> convertUnits(ListOf<UnitDefinition> sbmlUnits) {
        Map<String, Unit<?>> units = new HashMap<>();
        for (UnitDefinition unitDefinition : sbmlUnits) {
            units.put(unitDefinition.getId(), convertUnit(unitDefinition));
        }
        return units;
    }

    public Unit<?> convertUnit(UnitDefinition unitDefinition) {
        Unit<?> resultUnit = new ProductUnit();
        for (org.sbml.jsbml.Unit sbmlUnit : unitDefinition.getListOfUnits()) {
            Unit unitComponent = getUnitForKind(sbmlUnit.getKind());
            unitComponent = unitComponent.transform(
                    UnitPrefixes.getUnitPrefixFromScale(sbmlUnit.getScale()).getCorrespondingConverter());
            unitComponent = unitComponent.pow((int) sbmlUnit.getExponent());
            unitComponent = unitComponent.multiply(sbmlUnit.getMultiplier());
            resultUnit = resultUnit.multiply(unitComponent);
        }
        logger.debug("Parsed unit {},", resultUnit.toString());
        return resultUnit;
    }

    private Unit<?> getUnitForKind(org.sbml.jsbml.Unit.Kind kind) {
        switch (kind) {
            case AMPERE:
                return AMPERE;
            case AVOGADRO:
                return ONE.multiply(6.022140857E23);
            case BECQUEREL:
                return BECQUEREL;
            case CANDELA:
                return CANDELA;
            case CELSIUS:
                return CELSIUS;
            case COULOMB:
                return COULOMB;
            case DIMENSIONLESS:
                return ONE;
            case FARAD:
                return FARAD;
            case GRAM:
                return GRAM;
            case GRAY:
                return GRAY;
            case HENRY:
                return HENRY;
            case HERTZ:
                return HERTZ;
            case ITEM:
                return ONE;
            case JOULE:
                return JOULE;
            case KATAL:
                return KATAL;
            case KELVIN:
                return KELVIN;
            case KILOGRAM:
                return KILOGRAM;
            case LITER:
                return LITRE;
            case LITRE:
                return LITRE;
            case LUMEN:
                return LUMEN;
            case LUX:
                return LUX;
            case METER:
                return METRE;
            case METRE:
                return METRE;
            case MOLE:
                return MOLE;
            case NEWTON:
                return NEWTON;
            case OHM:
                return OHM;
            case PASCAL:
                return PASCAL;
            case RADIAN:
                return RADIAN;
            case SECOND:
                return SECOND;
            case SIEMENS:
                return SIEMENS;
            case SIEVERT:
                return SIEVERT;
            case STERADIAN:
                return STERADIAN;
            case TESLA:
                return TESLA;
            case VOLT:
                return VOLT;
            case WATT:
                return WATT;
            case WEBER:
                return WEBER;
            default:
                return ONE;
        }
    }

}
