package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractQuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class EndocytosisCheckpointConcentration extends AbstractQuantitativeFeature<MolarConcentration> {

    public EndocytosisCheckpointConcentration(Quantity<MolarConcentration> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<MolarConcentration> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<MolarConcentration> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<MolarConcentration>, EndocytosisCheckpointConcentration, Builder> {

        public Builder(Quantity<MolarConcentration> quantity) {
            super(quantity);
        }

        @Override
        protected EndocytosisCheckpointConcentration createObject(Quantity<MolarConcentration> quantity) {
            return new EndocytosisCheckpointConcentration(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
