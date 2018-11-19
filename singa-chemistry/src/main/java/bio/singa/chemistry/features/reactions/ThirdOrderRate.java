package bio.singa.chemistry.features.reactions;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Unit;

import static tec.uom.se.AbstractUnit.ONE;


/**
 * @author cl
 */
public interface ThirdOrderRate extends ReactionRate<ThirdOrderRate> {

    static Unit<ThirdOrderRate> getConsistentUnit() {
        return ONE.divide(UnitRegistry.getConcentrationUnit().multiply(UnitRegistry.getTimeUnit())).asType(ThirdOrderRate.class);
    }

}
