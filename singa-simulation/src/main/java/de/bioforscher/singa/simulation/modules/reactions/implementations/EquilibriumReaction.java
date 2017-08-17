package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.features.model.FeatureOrigin;
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

    public EquilibriumReaction(Simulation simulation, Quantity<Frequency> forwardsRateConstant, Quantity<Frequency> backwardsRateConstant) {
        super(simulation);
        // features
        this.availableFeatures.add(ForwardsRateConstant.class);
        this.availableFeatures.add(BackwardsRateConstant.class);
        setFeature(new ForwardsRateConstant(forwardsRateConstant, FeatureOrigin.MANUALLY_ANNOTATED));
        setFeature(new BackwardsRateConstant(backwardsRateConstant, FeatureOrigin.MANUALLY_ANNOTATED));
        // deltas
        applyAlways();
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<Frequency> forwardsRateConstant = getScaledFeature(ForwardsRateConstant.class);
        final Quantity<Frequency> backwardsRateConstant = getScaledFeature(BackwardsRateConstant.class);
        // concentrations of substrates that influence the reaction
        double substrateConcentration = determineConcentration(concentrationContainer, ReactantRole.DECREASING);
        double productConcentration = determineConcentration(concentrationContainer, ReactantRole.INCREASING);
        // calculate acceleration
        return substrateConcentration * forwardsRateConstant.getValue().doubleValue() -
                productConcentration * backwardsRateConstant.getValue().doubleValue();
    }

}
