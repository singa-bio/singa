package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;

import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.DECREASING;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.INCREASING;

/**
 * Reversible reactions are {@link Reaction}s where the substrates form products, and products can also from substrates.
 * Reversible reactions follow the laws of mass action.
 * <pre>
 *     A <-> B
 * </pre>
 *
 * @author cl
 */
public class ReversibleReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private RateConstant forwardsReactionRate;
    private RateConstant backwardsReactionRate;

    @Override
    public void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        SectionDeltaFunction function = new SectionDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(ForwardsRateConstant.class);
        getRequiredFeatures().add(BackwardsRateConstant.class);
        // reference module in simulation
        addModuleToSimulation();
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity forwardsRateConstant = getScaledForwardsReactionRate();
        final Quantity backwardsRateConstant = getScaledBackwardsReactionRate();
        // concentrations of substrates that influence the reaction
        double substrateConcentration = determineEffectiveConcentration(concentrationContainer, DECREASING);
        double productConcentration = determineEffectiveConcentration(concentrationContainer, INCREASING);
        // calculate acceleration
        return substrateConcentration * forwardsRateConstant.getValue().doubleValue() -
                productConcentration * backwardsRateConstant.getValue().doubleValue();
    }

    @Override
    public String getReactionString() {
        String substrates = collectSubstrateString();
        String products = collectProductsString();
        if (Character.isWhitespace(substrates.charAt(0))) {
            substrates = substrates.substring(1);
        }
        return substrates + " \u21CB" + products;
    }

    @Override
    public void checkFeatures() {
        boolean forwardsRateFound = false;
        boolean backwardsRateFound = false;
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof ForwardsRateConstant) {
                forwardsRateFound = true;
            }
            // any backwards rate constant
            if (feature instanceof BackwardsRateConstant) {
                backwardsRateFound = true;
            }
        }
        if (!forwardsRateFound || !backwardsRateFound) {
            throw new FeatureUnassignableException("Required reaction rates unavailable.");
        }
    }

    private Quantity getScaledForwardsReactionRate() {
        if (forwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof ForwardsRateConstant) {
                    forwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (supplier.isStrutCalculation()) {
            return forwardsReactionRate.getHalfScaledQuantity();
        }
        return forwardsReactionRate.getScaledQuantity();

    }

    private Quantity getScaledBackwardsReactionRate() {
        if (backwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof BackwardsRateConstant) {
                    backwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (supplier.isStrutCalculation()) {
            return backwardsReactionRate.getHalfScaledQuantity();
        }
        return backwardsReactionRate.getScaledQuantity();
    }

    public static class Builder extends Reaction.Builder<ReversibleReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected ReversibleReaction createObject(Simulation simulation) {
            ReversibleReaction module = ModuleFactory.setupModule(ReversibleReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.SECTION_SPECIFIC);
            module.setSimulation(simulation);
            return module;
        }

        public Builder forwardsRateConstant(RateConstant forwardsRateConstant) {
            topLevelObject.setFeature(forwardsRateConstant);
            topLevelObject.forwardsReactionRate = forwardsRateConstant;
            return this;
        }

        public Builder backwardsRateConstant(RateConstant backwardsRateConstant) {
            topLevelObject.setFeature(backwardsRateConstant);
            topLevelObject.backwardsReactionRate = backwardsRateConstant;
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
