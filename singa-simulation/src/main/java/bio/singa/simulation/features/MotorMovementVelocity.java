package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
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
public class MotorMovementVelocity extends AbstractScalableQuantitativeFeature<Speed> {

    private static final Unit<Speed> NANOMETRE_PER_SECOND = new ProductUnit<>(NANO(METRE).divide(SECOND));

    public MotorMovementVelocity(Quantity<Speed> quantity) {
        super(quantity);
    }

    public static Builder of(double value, Unit<Speed> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static Builder of(Quantity<Speed> quantity) {
        return new Builder(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleForPixel(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Speed>, MotorMovementVelocity, Builder> {

        public Builder(Quantity<Speed> quantity) {
            super(quantity);
        }

        @Override
        protected MotorMovementVelocity createObject(Quantity<Speed> quantity) {
            return new MotorMovementVelocity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
