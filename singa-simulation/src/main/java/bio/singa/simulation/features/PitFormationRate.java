package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class PitFormationRate extends AbstractScalableQuantitativeFeature<SpawnRate> {

    public PitFormationRate(Quantity<SpawnRate> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<SpawnRate> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<SpawnRate> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<SpawnRate>, PitFormationRate, Builder> {

        public Builder(Quantity<SpawnRate> quantity) {
            super(quantity);
        }

        @Override
        protected PitFormationRate createObject(Quantity<SpawnRate> quantity) {
            return new PitFormationRate(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
