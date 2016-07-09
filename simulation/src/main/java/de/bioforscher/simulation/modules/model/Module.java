package de.bioforscher.simulation.modules.model;

import de.bioforscher.simulation.model.AutomatonGraph;

/**
 * Created by Christoph on 06.07.2016.
 */
public interface Module {

    void applyTo(AutomatonGraph graph);

}
