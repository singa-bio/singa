package bio.singa.simulation.events;

import bio.singa.simulation.model.graphs.AutomatonGraph;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

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

    private Quantity<Time> elapsedTime;

    /**
     * Creates a new GraphUpdatedEvent.
     * @param graph The graph.
     * @param elapsedTime The time at which the update was emitted.
     */
    public GraphUpdatedEvent(AutomatonGraph graph, Quantity<Time> elapsedTime) {
        this.graph = graph;
        this.elapsedTime = elapsedTime;
    }

    /**
     * Returns the encapsulated graph.
     * @return The encapsulated graph.
     */
    public AutomatonGraph getGraph() {
        return graph;
    }

    public Quantity<Time> getElapsedTime() {
        return elapsedTime;
    }
}
