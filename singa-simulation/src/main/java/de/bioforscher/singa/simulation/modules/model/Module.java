package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * @author cl
 */
public interface Module {

    void determineAllDeltas();

    LocalError determineDeltasForNode(AutomatonNode node);

    LocalError getLargestLocalError();

    void resetLargestLocalError();
}
