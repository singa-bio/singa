package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static tech.units.indriya.AbstractUnit.ONE;

/**
 * Snare pairs required for fusion event to trigger
 * @author cl
 */
public class SNAREFusionPairs extends AbstractQuantitativeFeature<Dimensionless> {

    public SNAREFusionPairs(Quantity<Dimensionless> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<Dimensionless> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double number) {
        return new Builder(Quantities.getQuantity(number, ONE));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Dimensionless>, SNAREFusionPairs, Builder> {

        public Builder(Quantity<Dimensionless> quantity) {
            super(quantity);
        }

        @Override
        protected SNAREFusionPairs createObject(Quantity<Dimensionless> quantity) {
            return new SNAREFusionPairs(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
