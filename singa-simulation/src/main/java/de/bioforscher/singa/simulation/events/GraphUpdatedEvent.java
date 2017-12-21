package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;

/**
 * @author cl
 */
public class GraphUpdatedEvent {

    private final AutomatonGraph graph;

    public GraphUpdatedEvent(AutomatonGraph graph) {
        this.graph = graph;
    }

    public AutomatonGraph getGraph() {
        return graph;
    }
}
