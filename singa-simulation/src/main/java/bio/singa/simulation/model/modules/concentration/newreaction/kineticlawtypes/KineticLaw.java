package bio.singa.simulation.model.modules.concentration.newreaction.kineticlawtypes;

import bio.singa.simulation.model.modules.concentration.newreaction.ReactionEvent;

/**
 * @author cl
 */
public interface KineticLaw {

    double determineVelocity(ReactionEvent reactionEvent);

}
