package de.bioforscher.singa.simulation.modules.newmodules.imlementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.functions.SectionDeltaFunction;
import de.bioforscher.singa.simulation.modules.newmodules.module.ModuleFactory;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.Simulation;

import javax.measure.Quantity;

import static de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole.DECREASING;
import static de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole.INCREASING;

/**
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
