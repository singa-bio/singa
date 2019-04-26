package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantConcentration;

/**
 * @author cl
 */
public class MichaelisMentenKineticLaw extends AbstractKineticLaw {

    public MichaelisMentenKineticLaw(Reaction reaction) {
        super(reaction);
        reaction.getRequiredFeatures().add(MichaelisConstant.class);
        reaction.getRequiredFeatures().add(TurnoverNumber.class);
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // reaction rates for this reaction
        final double kCat = getScaledRate(TurnoverNumber.class);
        final double km = getRate(MichaelisConstant.class).getContent().getValue().doubleValue();
        // (KCAT * enzyme * substrate) / KM + substrate
        ReactantConcentration substrateConcentration = reactionEvent.getUpdatableBehavior().collectSubstrates().iterator().next();
        ReactantConcentration enzymeConcentration = reactionEvent.getUpdatableBehavior().collectCatalysts().iterator().next();
        return (kCat * enzymeConcentration.getConcentration() * substrateConcentration.getConcentration()) / (km + substrateConcentration.getConcentration());
    }
}
