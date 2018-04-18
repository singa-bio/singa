package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;

/**
 * @author cl
 */
public interface KineticLaw {

    double calculateVelocity(ConcentrationContainer concentrationContainer);

}
