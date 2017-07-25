package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.timing.LocalError;

/**
 * @author cl
 */
public interface Module {

    void determineAllDeltas();

    LocalError determineDeltasForNode(BioNode node);

    LocalError getLargestLocalError();

    void resetLargestLocalError();
}
