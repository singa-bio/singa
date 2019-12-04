package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static bio.singa.chemistry.features.reactions.RateConstant.Direction.BACKWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Direction.FORWARDS;
import static bio.singa.chemistry.features.reactions.RateConstant.Order.*;
import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public abstract class RateConstant<ReactionRateType extends ReactionRate<ReactionRateType>> extends AbstractScalableQuantitativeFeature<ReactionRateType> {

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
        BuilderStep evidence(Evidence... evidence);
        RateConstant build();
    }

    public interface BuilderStep {
        BuilderStep comment(String string);
        RateConstant build();
    }

    public static class RateBuilder implements DirectionStep, OrderStep, ConcentrationStep, TimeStep, OriginStep, BuilderStep {

        private Direction direction;
        private Order order;
        private double value;
        private List<Evidence> evidence;
        private Unit<Time> timeUnit;
        private Unit<MolarConcentration> concentrationUnit;
        private String comment;

        public RateBuilder(double value) {
            this.value = value;
            evidence = new ArrayList<>();
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
        public BuilderStep evidence(Evidence... evidence) {
            this.evidence.addAll(Arrays.asList(evidence));
            return this;
        }

        @Override
        public BuilderStep comment(String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public RateConstant build() {
            RateConstant<?> rateConstant = null;
            if (direction == FORWARDS && order == ZERO) {
                ProductUnit<ZeroOrderRate> unit = new ProductUnit<>(concentrationUnit.divide(timeUnit));
                rateConstant = new ZeroOrderForwardsRateConstant(Quantities.getQuantity(value, unit));
            }
            if (direction == FORWARDS && order == FIRST) {
                ProductUnit<FirstOrderRate> unit = new ProductUnit<>(ONE.divide(timeUnit));
                rateConstant = new FirstOrderForwardsRateConstant(Quantities.getQuantity(value, unit));
            }
            if (direction == FORWARDS && order == SECOND) {
                ProductUnit<SecondOrderRate> unit = new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit)));
                rateConstant = new SecondOrderForwardsRateConstant(Quantities.getQuantity(value, unit));
            }

            if (direction == BACKWARDS && order == ZERO) {
                ProductUnit<ZeroOrderRate> unit = new ProductUnit<>(concentrationUnit.divide(timeUnit));
                rateConstant = new ZeroOrderBackwardsRateConstant(Quantities.getQuantity(value, unit));
            }
            if (direction == BACKWARDS && order == FIRST) {
                ProductUnit<FirstOrderRate> unit = new ProductUnit<>(ONE.divide(timeUnit));
                rateConstant = new FirstOrderBackwardsRateConstant(Quantities.getQuantity(value, unit));
            }
            if (direction == BACKWARDS && order == SECOND) {
                ProductUnit<SecondOrderRate> unit = new ProductUnit<>(ONE.divide(concentrationUnit.multiply(timeUnit)));
                rateConstant = new SecondOrderBackwardsRateConstant(Quantities.getQuantity(value, unit));
            }
            if (rateConstant == null) {
                throw new IllegalStateException("Reaction Rate cannot be created with the given parameters.");
            }
            evidence.forEach(rateConstant::addEvidence);
            rateConstant.setComment(comment);
            return rateConstant;
        }
    }

}
