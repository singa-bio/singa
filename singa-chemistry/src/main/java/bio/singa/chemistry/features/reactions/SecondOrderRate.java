package bio.singa.chemistry.features.reactions;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Unit;

import static tec.units.indriya.AbstractUnit.ONE;

/**
 * Second order rates are in concentration^-1 * time^-1.
 *
 * @author cl
 */
public interface SecondOrderRate extends ReactionRate<SecondOrderRate> {

    static Unit<SecondOrderRate> getConsistentUnit() {
        return ONE.divide(UnitRegistry.getConcentrationUnit().multiply(UnitRegistry.getTimeUnit())).asType(SecondOrderRate.class);
    }

}
