package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class ActinBoostVelocity extends AbstractScalableQuantitativeFeature<Speed> {

    public static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    public ActinBoostVelocity(Quantity<Speed> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<Speed> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Speed> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Speed>, ActinBoostVelocity, Builder> {

        public Builder(Quantity<Speed> quantity) {
            super(quantity);
        }

        @Override
        protected ActinBoostVelocity createObject(Quantity<Speed> quantity) {
            return new ActinBoostVelocity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
