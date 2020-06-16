package bio.singa.chemistry.features.structure3d;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class Radius extends AbstractQuantitativeFeature<Length> {

    public Radius(Quantity<Length> quantity) {
        super(quantity);
    }

    public static Builder of(double value, Unit<Length> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Length>, Radius, Builder> {

        public Builder(Quantity<Length> quantity) {
            super(quantity);
        }

        @Override
        protected Radius createObject(Quantity<Length> quantity) {
            return new Radius(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
