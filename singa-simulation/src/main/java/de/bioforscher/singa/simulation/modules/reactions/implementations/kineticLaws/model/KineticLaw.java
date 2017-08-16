package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;

/**
 * @author leberech
 */
public interface KineticLaw {

    double calculateVelocity(ConcentrationContainer concentrationContainer);

}
