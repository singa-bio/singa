package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
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
    static {
        requiredFeatures.add(RateConstant.class);
    }

    private NthOrderReaction(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rate for this reaction
        final Quantity<Frequency> reactionRate = getScaledFeature(RateConstant.class);
        // concentrations of substrates that influence the reaction
        double concentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.DECREASING);
        // calculate acceleration
        return concentration * reactionRate.getValue().doubleValue();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
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
