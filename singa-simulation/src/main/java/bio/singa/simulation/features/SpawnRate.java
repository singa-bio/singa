package bio.singa.simulation.features;

import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Area;

import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public interface SpawnRate extends Quantity<SpawnRate> {

    ProductUnit<Area> SQUARE_NANOMETRE = new ProductUnit<>(NANO(METRE).pow(2));
    ProductUnit<SpawnRate> PER_SQUARE_NANOMETRE_PER_SECOND = new ProductUnit<>(ONE.divide(SQUARE_NANOMETRE.multiply(SECOND)));
    ProductUnit<Area> SQUARE_MICRO_METRE = new ProductUnit<>(MICRO(METRE).pow(2));
    ProductUnit<SpawnRate> PER_SQUARE_MICRO_METRE_PER_SECOND = new ProductUnit<>(ONE.divide(SQUARE_MICRO_METRE.multiply(SECOND)));

}
