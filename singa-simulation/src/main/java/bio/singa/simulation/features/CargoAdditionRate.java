package bio.singa.simulation.features;

import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.FirstOrderRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.features.model.AbstractFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class CargoAdditionRate extends FirstOrderRateConstant implements ForwardsRateConstant<FirstOrderRate> {

    public CargoAdditionRate(Quantity<FirstOrderRate> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<FirstOrderRate> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<FirstOrderRate> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<FirstOrderRate>, CargoAdditionRate, Builder> {

        public Builder(Quantity<FirstOrderRate> quantity) {
            super(quantity);
        }

        @Override
        protected CargoAdditionRate createObject(Quantity<FirstOrderRate> quantity) {
            return new CargoAdditionRate(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
