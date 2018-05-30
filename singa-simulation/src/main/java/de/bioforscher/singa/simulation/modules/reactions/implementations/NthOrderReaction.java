package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class NthOrderReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    private RateConstant rateConstant;

    static {
        requiredFeatures.add(RateConstant.class);
    }

    private NthOrderReaction(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // concentrations of substrates that influence the reaction
        double concentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.DECREASING);
        // calculate acceleration
        return concentration * getScaledReactionRate().getValue().doubleValue();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }

    @Override
    public void checkFeatures() {
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof RateConstant) {
                return;
            }
        }
        throw new FeatureUnassignableException("Required reaction rate unavailable.");
    }

    private Quantity getScaledReactionRate() {
        if (rateConstant == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof RateConstant) {
                    rateConstant = (RateConstant) feature;
                    break;
                }
            }
        }
        if (halfTime) {
            return rateConstant.getHalfScaledQuantity();
        }
        return rateConstant.getScaledQuantity();
    }

    public static class Builder extends Reaction.Builder<NthOrderReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected NthOrderReaction createObject(Simulation simulation) {
            return new NthOrderReaction(simulation);
        }

        public Builder rateConstant(RateConstant rateConstant) {
            topLevelObject.setFeature(rateConstant);
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
