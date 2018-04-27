package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
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
public class EquilibriumReaction extends Reaction {

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();
    static {
        requiredFeatures.add(ForwardsRateConstant.class);
        requiredFeatures.add(BackwardsRateConstant.class);
    }


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
