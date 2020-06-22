package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class EndocytosisCheckpointTime extends AbstractQuantitativeFeature<Time> {

    public EndocytosisCheckpointTime(Quantity<Time> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<Time> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Time> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Time>, EndocytosisCheckpointTime, Builder> {

        public Builder(Quantity<Time> quantity) {
            super(quantity);
        }

        @Override
        protected EndocytosisCheckpointTime createObject(Quantity<Time> quantity) {
            return new EndocytosisCheckpointTime(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
