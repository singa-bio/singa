package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class MembraneTickness extends AbstractQuantitativeFeature<Length> {

    public MembraneTickness(Quantity<Length> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<Length> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Length> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Length>, MembraneTickness, Builder> {

        public Builder(Quantity<Length> quantity) {
            super(quantity);
        }

        @Override
        protected MembraneTickness createObject(Quantity<Length> quantity) {
            return new MembraneTickness(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
