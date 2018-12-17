package bio.singa.simulation.features;

import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Area;

import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public interface SpawnRate extends Quantity<SpawnRate> {

    ProductUnit<Area> SQUARE_NANOMETRE = new ProductUnit<>(NANO(METRE).pow(2));
    ProductUnit<SpawnRate> PER_SQUARE_NANOMETRE_PER_SECOND = new ProductUnit<>(ONE.divide(SQUARE_NANOMETRE.multiply(SECOND)));

}
