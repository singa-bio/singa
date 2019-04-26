package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;

/**
 * @author cl
 */
public interface KineticLaw {

    double determineVelocity(ReactionEvent reactionEvent);

}
