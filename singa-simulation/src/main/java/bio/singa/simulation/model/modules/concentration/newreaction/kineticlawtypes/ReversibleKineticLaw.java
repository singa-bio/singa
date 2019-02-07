package bio.singa.simulation.model.modules.concentration.newreaction.kineticlawtypes;

import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.simulation.model.modules.concentration.newreaction.Reaction;
import bio.singa.simulation.model.modules.concentration.newreaction.ReactionEvent;

/**
 * @author cl
 */
public class ReversibleKineticLaw extends AbstractKineticLaw {

    public ReversibleKineticLaw(Reaction reaction) {
        super(reaction);
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // get rates
        final double forwardsRateConstant = getScaledRate(ForwardsRateConstant.class);
        final double backwardsRateConstant = getScaledRate(BackwardsRateConstant.class);
        // multiply substrates
        double substrateConcentration = multiply(reactionEvent.getUpdatableBehavior().collectSubstrates());
        // multiply products
        double productConcentration = multiply(reactionEvent.getUpdatableBehavior().collectProducts());
        // calculate velocity
        return forwardsRateConstant * substrateConcentration - backwardsRateConstant * productConcentration;
    }

}
