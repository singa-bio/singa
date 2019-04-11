package bio.singa.chemistry.features.reactions;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Unit;

import static tec.units.indriya.AbstractUnit.ONE;

/**
 * First order rates are in time^-1.
 *
 * @author cl
 */
public interface FirstOrderRate extends ReactionRate<FirstOrderRate> {

    static Unit<FirstOrderRate> getConsistentUnit() {
        return ONE.divide(UnitRegistry.getTimeUnit()).asType(FirstOrderRate.class);
    }

}
