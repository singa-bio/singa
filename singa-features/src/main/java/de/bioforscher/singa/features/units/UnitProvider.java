package de.bioforscher.singa.features.units;


import de.bioforscher.singa.features.quantities.DynamicViscosity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.ReactionRate;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Unit;

import static tec.units.ri.AbstractUnit.ONE;
import static tec.units.ri.unit.Units.*;

/**
 * This class defines some units commonly used in systems biology, that ar not explicitly attributed to any feature.
 *
 * @author cl
 */
public final class UnitProvider {

    /**
     * Molar concentration, also called molarity, amount concentration or
     * substance concentration, is a measure of the concentration of a solute in
     * a solution, or of any chemical species in terms of amount of substance in
     * a given volume.
     */
    public static final Unit<MolarConcentration> MOLE_PER_LITRE = new ProductUnit<>(MOLE.divide(LITRE));

    /**
     * For Reaction that are occurring secondly.
     */
    public static final Unit<ReactionRate> PER_SECOND = new ProductUnit<>(ONE.divide(SECOND));

    /**
     * For Reaction that are occurring minutely.
     */
    public static final Unit<ReactionRate> PER_MINUTE = new ProductUnit<>(ONE.divide(MINUTE));

    /**
     * The SI unit for dynamic viscosity quantities.
     */
    public static final Unit<DynamicViscosity> PASCAL_SECOND = new ProductUnit<>(PASCAL.multiply(SECOND));

}
