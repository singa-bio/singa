package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class EquilibriumReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    static {
        requiredFeatures.add(ForwardsRateConstant.class);
        requiredFeatures.add(BackwardsRateConstant.class);
    }

    private RateConstant forwardsReactionRate;
    private RateConstant backwardsReactionRate;

    private EquilibriumReaction(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity forwardsRateConstant = getScaledForwardsReactionRate();
        final Quantity backwardsRateConstant = getScaledBackwardsReactionRate();
        // concentrations of substrates that influence the reaction
        double substrateConcentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.DECREASING);
        double productConcentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.INCREASING);
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
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
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
        if (halfTime) {
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
        if (halfTime) {
            return backwardsReactionRate.getHalfScaledQuantity();
        }
        return backwardsReactionRate.getScaledQuantity();
    }

    public static class Builder extends Reaction.Builder<EquilibriumReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected EquilibriumReaction createObject(Simulation simulation) {
            return new EquilibriumReaction(simulation);
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
