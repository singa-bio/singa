package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

/**
 * The Event that is emitted from the {@link NodeEventEmitter}, encapsulating a node.
 *
 * @author cl
 */
public class NodeUpdatedEvent {

    /**
     * The time the event was emitted.
     */
    private final Quantity<Time> time;

    /**
     * The encapsulated node.
     */
    private final AutomatonNode node;

    /**
     * Creates a new NodeUpdatedEvent.
     * @param time The time the event was emitted.
     * @param node The encapsulated node.
     */
    public NodeUpdatedEvent(Quantity<Time> time, AutomatonNode node) {
        this.time = time;
        this.node = node;
    }

    /**
     * Returns the time the event was emitted.
     * @return The time the event was emitted.
     */
    public Quantity<Time> getTime() {
        return time;
    }

    /**
     * Returns the encapsulated node
     * @return The encapsulated node
     */
    public AutomatonNode getNode() {
        return node;
    }

}
