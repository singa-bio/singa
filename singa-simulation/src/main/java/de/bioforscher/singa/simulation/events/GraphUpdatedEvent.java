package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;

/**
 * Created by Christoph on 01.08.2016.
 */
public class GraphUpdatedEvent {

    private final AutomatonGraph graph;

    public GraphUpdatedEvent(AutomatonGraph graph) {
        this.graph = graph;
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }
}
