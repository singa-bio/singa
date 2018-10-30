package bio.singa.chemistry.features.reactions;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Unit;

/**
 * Zero order rates are in concentration * time^-1.
 *
 * @author cl
 */
public interface ZeroOrderRate extends ReactionRate<ZeroOrderRate> {

    static Unit<ZeroOrderRate> getConsistentUnit() {
        return UnitRegistry.getConcentrationUnit().divide(UnitRegistry.getTimeUnit()).asType(ZeroOrderRate.class);
    }

}
