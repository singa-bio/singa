package de.bioforscher.singa.features.units;


import de.bioforscher.singa.features.quantities.DynamicViscosity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import static tec.uom.se.unit.Units.*;

/**
 * This class defines some units commonly used in systems biology, that ar not explicitly attributed to any feature.
 *
 * @author cl
 */
public final class UnitProvider {

    /**
     * Molar concentration, also called molarity, amount concentration or substance concentration, is a measure of the
     * concentration of a solute in a solution, or of any chemical species in terms of amount of substance in a given
     * volume.
     */
    public static final Unit<MolarConcentration> MOLE_PER_LITRE = new ProductUnit<>(MOLE.divide(LITRE));

    /**
     * The SI unit for dynamic viscosity quantities.
     */
    public static final Unit<DynamicViscosity> PASCAL_SECOND = new ProductUnit<>(PASCAL.multiply(SECOND));

    /**
     * The Angstroem is a unit of length equal to 10e−10 m (one ten-billionth of a metre) or 0.1 nanometre.
     * @see <a href="https://en.wikipedia.org/wiki/%C3%85ngstr%C3%B6m">Wikipedia: Angstroem</a>
     */
    public static final Unit<Length> ANGSTROEM = METRE.multiply(10e-10);

}
