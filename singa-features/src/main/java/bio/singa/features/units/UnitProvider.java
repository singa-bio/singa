package bio.singa.features.units;


import bio.singa.features.quantities.DynamicViscosity;
import bio.singa.features.quantities.MolarConcentration;
import tec.uom.se.format.SimpleUnitFormat;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;

import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * This class defines some units commonly used in systems biology, that are not explicitly attributed to any feature.
 *
 * @author cl
 */
public final class UnitProvider {

    public static List<Unit<Time>> TIME_UNITS = new ArrayList<>();
    static {
        TIME_UNITS.add(NANO(SECOND));
        TIME_UNITS.add(MICRO(SECOND));
        TIME_UNITS.add(MILLI(SECOND));
        TIME_UNITS.add(SECOND);
        TIME_UNITS.add(MINUTE);
        TIME_UNITS.add(HOUR);
        TIME_UNITS.add(DAY);
        TIME_UNITS.add(WEEK);
        TIME_UNITS.add(YEAR);
    }


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
     *
     * @see <a href="https://en.wikipedia.org/wiki/%C3%85ngstr%C3%B6m">Wikipedia: Angstroem</a>
     */
    public static final Unit<Length> ANGSTROEM = METRE.divide(10000000000L);
    static {
        SimpleUnitFormat.getInstance().label(ANGSTROEM, "\u212B");
    }



}
