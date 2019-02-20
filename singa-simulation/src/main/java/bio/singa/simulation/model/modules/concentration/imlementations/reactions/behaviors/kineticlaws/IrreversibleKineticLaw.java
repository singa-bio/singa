package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.ZeroOrderRateConstant;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;

/**
 * @author cl
 */
public class IrreversibleKineticLaw extends AbstractKineticLaw {

    public IrreversibleKineticLaw(Reaction reaction) {
        super(reaction);
        reaction.getRequiredFeatures().add(ForwardsRateConstant.class);
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // get rates
        final double forwardsRateConstant = getScaledRate(ForwardsRateConstant.class);
        Feature rate = getRate(ForwardsRateConstant.class);
        if (rate instanceof ZeroOrderRateConstant) {
            return forwardsRateConstant;
        }
        // multiply substrates
        double substrateConcentration = multiply(reactionEvent.getUpdatableBehavior().collectSubstrates());
        // calculate velocity
        return forwardsRateConstant * substrateConcentration;
    }

}
