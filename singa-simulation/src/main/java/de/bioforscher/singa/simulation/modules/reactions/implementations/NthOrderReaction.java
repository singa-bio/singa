package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
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
public class NthOrderReaction extends Reaction {

    public NthOrderReaction(Simulation simulation, Quantity<Frequency> rateConstant) {
        super(simulation);
        // feature
        availableFeatures.add(RateConstant.class);
        setFeature(new RateConstant(rateConstant, FeatureOrigin.MANUALLY_ANNOTATED));
        // deltas
        applyAlways();
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rate for this reaction
        final Quantity<Frequency> reactionRate = getScaledFeature(RateConstant.class);
        // concentrations of substrates that influence the reaction
        double concentration = determineConcentration(concentrationContainer, ReactantRole.DECREASING);
        // calculate acceleration
        return concentration * reactionRate.getValue().doubleValue();
    }

}
