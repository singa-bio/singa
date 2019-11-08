package bio.singa.features.quantities;


import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static tech.units.indriya.unit.Units.*;

/**
 * This class holds values and units of natural constants.
 *
 * @author cl
 */
public class NaturalConstants {

    /**
     * The Boltzmann constant is the constant of proportionality that relates the average kinetic energy of particles in
     * a gas with the temperature of the gas.
     *
     * @see <a href="https://physics.nist.gov/cgi-bin/cuu/Value?k">NIST</a>
     */
    public static Quantity BOLTZMANN_CONSTANT = Quantities.getQuantity(1.38064852E-23, new ProductUnit(JOULE.divide(KELVIN)));

    /**
     * The gas constant is the constant of proportionality that relates the energy scale in physics to the temperature
     * scale, when a mole of particles at the stated temperature is being considered.
     *
     * @see <a href="https://physics.nist.gov/cgi-bin/cuu/Value?r">NIST</a>
     */
    public static Quantity MOLAR_GAS_CONSTANT = Quantities.getQuantity(8.3144598, new ProductUnit(JOULE.divide(KELVIN).divide(MOLE)));

    /**
     * The avogadro constant is the number of constituent particles, usually atoms or molecules, that are contained in
     * the amount of substance given by one mole.
     *
     * @see <a href="https://physics.nist.gov/cgi-bin/cuu/Value?na">NIST</a>
     */
    public static Quantity AVOGADRO_CONSTANT = Quantities.getQuantity(6.022140857e23, new ProductUnit(AbstractUnit.ONE.divide(MOLE)));

    /**
     * The Euler–Mascheroni constant is defined as the limiting difference between the harmonic series and the natural
     * logarithm.
     *
     * @see <a href="http://dlmf.nist.gov/3.12.E4">NIST</a>
     */
    public static Quantity<Dimensionless> EULER_MASCHERONI_CONSTANT = Quantities.getQuantity(0.577215664901532, AbstractUnit.ONE);


}
