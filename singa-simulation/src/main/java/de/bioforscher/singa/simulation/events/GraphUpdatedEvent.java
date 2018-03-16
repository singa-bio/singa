package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;

/**
 * The Event that is emitted from the {@link GraphEventEmitter}, encapsulating a graph.
 *
 * @author cl
 */
public class GraphUpdatedEvent {

    /**
     * The graph
     */
    private final AutomatonGraph graph;

    /**
     * Creates a new GraphUpdatedEvent.
     * @param graph The graph.
     */
    public GraphUpdatedEvent(AutomatonGraph graph) {
        this.graph = graph;
    }

    /**
     * Returns the encapsulated graph.
     * @return The encapsulated graph.
     */
    public AutomatonGraph getGraph() {
        return graph;
    }

}
