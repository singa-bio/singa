package bio.singa.chemistry.features.diffusivity;

import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tech.units.indriya.unit.MetricPrefix.CENTI;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * Diffusivity or diffusion coefficient is a proportionality constant between the molar flux due to molecular diffusion
 * and the gradient in the concentration of the species (or the driving force for diffusion). The higher the diffusivity
 * (of one substance with respect to another), the faster they diffuse into each other.
 *
 * @author cl
 */
public interface Diffusivity extends Quantity<Diffusivity> {

    Unit<Diffusivity> SQUARE_MICROMETRE_PER_SECOND = new ProductUnit<>(MICRO(METRE).pow(2).divide(SECOND));
    Unit<Diffusivity> SQUARE_CENTIMETRE_PER_SECOND = new ProductUnit<>(CENTI(METRE).pow(2).divide(SECOND));
    Unit<Diffusivity> SQUARE_METRE_PER_SECOND = new ProductUnit<>(METRE.pow(2).divide(SECOND));

}
