package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public class Ratio extends AbstractQuantitativeFeature<Dimensionless> {

    public Ratio(Quantity<Dimensionless> quantity) {
        super(quantity);
    }

    public Ratio(double quantity) {
        super(Quantities.getQuantity(quantity, ONE));
    }

    public static Builder of(double value) {
        return new Builder(Quantities.getQuantity(value, ONE));
    }

    public static Builder of(Quantity<Dimensionless> quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Dimensionless>, Ratio, Builder> {

        public Builder(Quantity<Dimensionless> quantity) {
            super(quantity);
        }

        @Override
        protected Ratio createObject(Quantity<Dimensionless> quantity) {
            return new Ratio(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
