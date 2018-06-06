package de.bioforscher.singa.simulation.modules.newmodules.specifity;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;

/**
 * @author cl
 */
public interface UpdateSpecificity {

    void processContainer(ConcentrationContainer container);

    void determineDeltas(ConcentrationContainer container);

}
