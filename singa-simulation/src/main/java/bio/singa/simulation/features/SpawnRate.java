package bio.singa.simulation.features;

import tec.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Area;

import static tec.units.indriya.AbstractUnit.ONE;
import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;
import static tec.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public interface SpawnRate extends Quantity<SpawnRate> {

    ProductUnit<Area> SQUARE_NANOMETRE = new ProductUnit<>(NANO(METRE).pow(2));
    ProductUnit<SpawnRate> PER_SQUARE_NANOMETRE_PER_SECOND = new ProductUnit<>(ONE.divide(SQUARE_NANOMETRE.multiply(SECOND)));

}
