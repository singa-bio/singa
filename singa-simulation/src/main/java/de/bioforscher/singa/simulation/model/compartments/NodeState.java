package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * The general state of a {@link AutomatonNode}.
 *
 * @author cl
 */
public enum NodeState {

    /**
     * Aqueous (mostly extracellular) environments.
     */
    AQUEOUS,

    /**
     * Cellular environments.
     */
    CYTOSOL,

    /**
     * Membrane environments.
     */
    MEMBRANE


}
