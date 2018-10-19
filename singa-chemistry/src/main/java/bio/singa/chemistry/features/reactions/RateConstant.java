package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.features.quantities.MolarConcentration;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

import static bio.singa.chemistry.features.reactions.RateConstant.Direction.BACKWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Direction.FORWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Order.*;
import static tec.uom.se.AbstractUnit.ONE;

/**
 * @author cl
 */
public abstract class RateConstant<ReactionRateType extends ReactionRate<ReactionRateType>> extends ScalableQuantityFeature<ReactionRateType> {

    public static DirectionStep create(double value) {
        return new RateBuilder(value);
    }

    public enum Direction {
        FORWARDS, BACKWARDS
    }

    public enum Order {
        ZERO, FIRST, SECOND
    }

    protected RateConstant(Quantity<ReactionRateType> quantityTypeQuantity, FeatureOrigin featureOrigin) {
        super(quantityTypeQuantity, featureOrigin);
    }

    public interface DirectionStep {
        OrderStep forward();

        OrderStep backward();
    }

    public interface OrderStep {
        ConcentrationStep zeroOrder();

        TimeStep firstOrder();

        ConcentrationStep secondOrder();
    }

    public interface ConcentrationStep {
        TimeStep concentrationUnit(Unit<MolarConcentration> concentrationUnit);
    }

    public interface TimeStep {
        OriginStep timeUnit(Unit<Time> timeUnit);
    }

    public interface OriginStep {
        BuilderStep origin(FeatureOrigin origin);

        RateConstant build();
    }

    public interface BuilderStep {
        RateConstant build();
    }

    public static class RateBuilder implements DirectionStep, OrderStep, ConcentrationStep, TimeStep, OriginStep, BuilderStep {

        private Direction direction;
        private Order order;
        private double value;
        private FeatureOrigin origin;
        private Unit<Time> timeUnit;
        private Unit<MolarConcentration> concentrationUnit;

        public RateBuilder(double value) {
            this.value = value;
        }

        @Override
        public OrderStep forward() {
            direction = FORWARDS;
            return this;
        }

        @Override
        public OrderStep backward() {
            direction = BACKWARDS;
            return this;
        }

        @Override
        public ConcentrationStep zeroOrder() {
            order = ZERO;
            return this;
        }

        @Override
        public TimeStep firstOrder() {
            order = FIRST;
            return this;
        }

        @Override
        public ConcentrationStep secondOrder() {
            order = SECOND;
            return this;
        }

        @Override
        public TimeStep concentrationUnit(Unit<MolarConcentration> concentrationUnit) {
            this.concentrationUnit = concentrationUnit;
            return this;
        }

        @Override
        public OriginStep timeUnit(Unit<Time> timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        @Override
        public BuilderStep origin(FeatureOrigin origin) {
            this.origin = origin;
            return this;
        }

        @Override
        public RateConstant build() {
            if (origin == null) {
                origin = FeatureOrigin.MANUALLY_ANNOTATED;
            }

            if (direction == FORWARDS && order == ZERO) {
                return new ZeroOrderForwardsRateConstant(value,
                        new ProductUnit<>(concentrationUnit.divide(timeUnit)), origin);
            }
            if (direction == FORWARDS && order == FIRST) {
                return new FirstOrderForwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(timeUnit)), origin);
            }
            if (direction == FORWARDS && order == SECOND) {
                return new SecondOrderForwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit))), origin);
            }

            if (direction == BACKWARDS && order == ZERO) {
                return new ZeroOrderBackwardsRateConstant(value,
                        new ProductUnit<>(concentrationUnit.divide(timeUnit)), origin);
            }
            if (direction == BACKWARDS && order == FIRST) {
                return new FirstOrderBackwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(timeUnit)), origin);
            }
            if (direction == BACKWARDS && order == SECOND) {
                return new SecondOrderBackwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit))), origin);
            }
            throw new IllegalStateException("Reaction Rate cannot be created with the given parameters.");
        }
    }

}
