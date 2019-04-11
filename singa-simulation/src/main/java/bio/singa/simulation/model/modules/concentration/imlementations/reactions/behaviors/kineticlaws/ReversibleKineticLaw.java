package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.ZeroOrderRateConstant;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;

/**
 * @author cl
 */
public class ReversibleKineticLaw extends AbstractKineticLaw {

    public ReversibleKineticLaw(Reaction reaction) {
        super(reaction);
        reaction.getRequiredFeatures().add(ForwardsRateConstant.class);
        reaction.getRequiredFeatures().add(BackwardsRateConstant.class);
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // get rates
        final double forwardsRateConstant = getScaledRate(ForwardsRateConstant.class);
        Feature forwardsRate = getRate(ForwardsRateConstant.class);
        if (forwardsRate instanceof ZeroOrderRateConstant) {
            return forwardsRateConstant;
        }
        final double backwardsRateConstant = getScaledRate(BackwardsRateConstant.class);
        Feature backwardsRate = getRate(BackwardsRateConstant.class);
        if (backwardsRate instanceof ZeroOrderRateConstant) {
            return forwardsRateConstant;
        }
        // multiply substrates
        double substrateConcentration = multiply(reactionEvent.getUpdatableBehavior().collectSubstrates());
        // multiply products
        double productConcentration = multiply(reactionEvent.getUpdatableBehavior().collectProducts());
        // calculate velocity
        return forwardsRateConstant * substrateConcentration - backwardsRateConstant * productConcentration;
    }

}
