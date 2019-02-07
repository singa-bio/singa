package bio.singa.simulation.model.modules.concentration.newreaction.kineticlawtypes;

import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.simulation.model.modules.concentration.newreaction.Reaction;
import bio.singa.simulation.model.modules.concentration.newreaction.ReactionEvent;

/**
 * @author cl
 */
public class IrreversibleKineticLaw extends AbstractKineticLaw {

    public IrreversibleKineticLaw(Reaction reaction) {
        super(reaction);
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // get rates
        final double forwardsRateConstant = getScaledRate(ForwardsRateConstant.class);
        // multiply substrates
        double substrateConcentration = multiply(reactionEvent.getUpdatableBehavior().collectSubstrates());
        // calculate velocity
        return forwardsRateConstant * substrateConcentration;
    }

}
