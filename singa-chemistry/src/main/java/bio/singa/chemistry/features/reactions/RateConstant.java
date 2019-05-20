package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.List;

import static bio.singa.chemistry.features.reactions.RateConstant.Direction.BACKWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Direction.FORWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Order.*;
import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public abstract class RateConstant<ReactionRateType extends ReactionRate<ReactionRateType>> extends ScalableQuantitativeFeature<ReactionRateType> {

    public static DirectionStep create(double value) {
        return new RateBuilder(value);
    }

    public enum Direction {
        FORWARDS, BACKWARDS
    }

    public enum Order {
        ZERO, FIRST, SECOND, THIRD
    }

    public RateConstant(Quantity<ReactionRateType> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public RateConstant(Quantity<ReactionRateType> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public RateConstant(Quantity<ReactionRateType> quantity) {
        super(quantity);
    }

    public interface DirectionStep {

        /**
         * Determines that the associated reaction uses this constant to parametrize the transformation from
         * substrates to products.
         *
         * @return order step
         */
        OrderStep forward();

        /**
         * Determines that the associated reaction uses this constant to parametrize the transformation from
         * products to substrates.
         *
         * @return order step
         */
        OrderStep backward();

    }

    public interface OrderStep {

        /**
         * Zero order rates are given in concentration * time^-1.
         * @return concentration step
         */
        ConcentrationStep zeroOrder();

        /**
         * First order rates are in time^-1.
         *
         * @return concentration step
         */
        TimeStep firstOrder();

        /**
         * Second order rates are given in concentration^-1 * time^-1.
         *
         * @return concentration step
         */
        ConcentrationStep secondOrder();

        /**
         * Second order rates are given in concentration^-2 * time^-1.
         *
         * @return concentration step
         */
        ConcentrationStep thirdOrder();

    }

    public interface ConcentrationStep {
        TimeStep concentrationUnit(Unit<MolarConcentration> concentrationUnit);
    }

    public interface TimeStep {
        OriginStep timeUnit(Unit<Time> timeUnit);
    }

    public interface OriginStep {
        BuilderStep evidence(Evidence evidence);

        RateConstant build();
    }

    public interface BuilderStep {
        RateConstant build();
    }

    public static class RateBuilder implements DirectionStep, OrderStep, ConcentrationStep, TimeStep, OriginStep, BuilderStep {

        private Direction direction;
        private Order order;
        private double value;
        private Evidence evidence;
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
        public ConcentrationStep thirdOrder() {
            order = THIRD;
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
        public BuilderStep evidence(Evidence evidence) {
            this.evidence = evidence;
            return this;
        }

        @Override
        public RateConstant build() {
            if (direction == FORWARDS && order == ZERO) {
                return new ZeroOrderForwardsRateConstant(value,
                        new ProductUnit<>(concentrationUnit.divide(timeUnit)), evidence);
            }
            if (direction == FORWARDS && order == FIRST) {
                return new FirstOrderForwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(timeUnit)), evidence);
            }
            if (direction == FORWARDS && order == SECOND) {
                return new SecondOrderForwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit))), evidence);
            }
            if (direction == FORWARDS && order == THIRD) {
                return new ThirdOrderForwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.pow(2).multiply(timeUnit))), evidence);
            }

            if (direction == BACKWARDS && order == ZERO) {
                return new ZeroOrderBackwardsRateConstant(value,
                        new ProductUnit<>(concentrationUnit.divide(timeUnit)), evidence);
            }
            if (direction == BACKWARDS && order == FIRST) {
                return new FirstOrderBackwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(timeUnit)), evidence);
            }
            if (direction == BACKWARDS && order == SECOND) {
                return new SecondOrderBackwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit))), evidence);
            }
            if (direction == BACKWARDS && order == THIRD) {
                return new ThirdOrderBackwardsRateConstant(value,
                        new ProductUnit<>(ONE.divide(concentrationUnit.pow(2).multiply(timeUnit))), evidence);
            }

            throw new IllegalStateException("Reaction Rate cannot be created with the given parameters.");
        }
    }

}
