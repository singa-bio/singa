package de.bioforscher.singa.units;

import de.bioforscher.singa.units.quantities.*;
import tec.units.ri.AbstractSystemOfUnits;
import tec.units.ri.AbstractUnit;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.HashMap;
import java.util.Map;

import static tec.units.ri.unit.Units.*;

import static tec.units.ri.AbstractUnit.ONE;

/**
 * This class defines some units commonly used in systems biology.
 *
 * @author cl
 */
public final class UnitProvider extends AbstractSystemOfUnits {

    /**
     * The singleton instance.
     */
    private static final UnitProvider INSTANCE = new UnitProvider();

    /**
     * Holds the mapping quantity to unit.
     */
    private final Map<Class<? extends Quantity>, AbstractUnit> quantityToUnit = new HashMap<>();

    /**
     * Diffusivity or diffusion coefficient is a proportionality constant
     * between the molar flux due to molecular diffusion and the gradient in the
     * concentration of the species (or the driving force for diffusion). The
     * higher the diffusivity (of one substance with respect to another), the
     * faster they diffuse into each other.
     */
    public static final Unit<Diffusivity> SQUARE_CENTIMETER_PER_SECOND = addUnit(
            new ProductUnit<>(METRE.divide(100).pow(2).divide(SECOND)), Diffusivity.class);

    /**
     * In chemistry, the molar mass is a physical property. It is defined as the
     * mass of a given substance (chemical element or chemical compound) divided
     * by its amount of substance.
     */
    public static final Unit<MolarMass> GRAM_PER_MOLE = addUnit(
            new ProductUnit<>(GRAM.divide(MOLE)), MolarMass.class);

    /**
     * Molar concentration, also called molarity, amount concentration or
     * substance concentration, is a measure of the concentration of a solute in
     * a solution, or of any chemical species in terms of amount of substance in
     * a given volume.
     */
    public static final Unit<MolarConcentration> MOLE_PER_LITRE = addUnit(
            new ProductUnit<>(MOLE.divide(LITRE)), MolarConcentration.class);

    /**
     * For Reaction that are occurring secondly.
     */
    public static final Unit<ReactionRate> PER_SECOND = addUnit(new ProductUnit<>(ONE.divide(SECOND)),
            ReactionRate.class);

    /**
     * For Reaction that are occurring minutely.
     */
    public static final Unit<ReactionRate> PER_MINUTE = addUnit(new ProductUnit<>(ONE.divide(MINUTE)),
            ReactionRate.class);

    /**
     * The SI unit for dynamic viscosity quantities.
     */
    public static final Unit<DynamicViscosity> PASCAL_SECOND = addUnit(
            new ProductUnit<>(PASCAL.multiply(SECOND)), DynamicViscosity.class);

    /**
     * Adds a new unit and maps it to the specified quantity type.
     *
     * @param unit the unit being added.
     * @param type the quantity type.
     * @return <code>unit</code>.
     */
    private static <U extends AbstractUnit<?>> U addUnit(U unit, Class<? extends Quantity<?>> type) {
        INSTANCE.units.add(unit);
        INSTANCE.quantityToUnit.put(type, unit);
        return unit;
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the metric system instance.
     */
    public static UnitProvider getInstance() {
        return INSTANCE;
    }


    /**
     * Default constructor (prevents this class from being instantiated).
     */
    private UnitProvider() {
    }

    @Override
    public String getName() {
        return "UnitProvider";
    }

    @Override
    public <Q extends Quantity<Q>> AbstractUnit<Q> getUnit(Class<Q> quantityType) {
        return this.quantityToUnit.get(quantityType);
    }

}
