package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

/**
 * @author cl
 */
public class EquilibriumReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private EquilibriumReaction(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<Frequency> forwardsRateConstant = getScaledFeature(ForwardsRateConstant.class);
        final Quantity<Frequency> backwardsRateConstant = getScaledFeature(BackwardsRateConstant.class);
        // concentrations of substrates that influence the reaction
        double substrateConcentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.DECREASING);
        double productConcentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.INCREASING);
        // calculate acceleration
        return substrateConcentration * forwardsRateConstant.getValue().doubleValue() -
                productConcentration * backwardsRateConstant.getValue().doubleValue();
    }

    public static class Builder extends Reaction.Builder<EquilibriumReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected EquilibriumReaction createObject(Simulation simulation) {
            return new EquilibriumReaction(simulation);
        }

        public Builder forwardsRateConstant(ForwardsRateConstant forwardsRateConstant) {
            topLevelObject.setFeature(forwardsRateConstant);
            return this;
        }

        public Builder backwardsRateConstant(BackwardsRateConstant backwardsRateConstant) {
            topLevelObject.setFeature(backwardsRateConstant);
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }


}
