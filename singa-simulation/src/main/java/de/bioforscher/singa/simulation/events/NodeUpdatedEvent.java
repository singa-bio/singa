package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

public class NodeUpdatedEvent {

    private final Quantity<Time> time;
    private final AutomatonNode node;

    public NodeUpdatedEvent(Quantity<Time> time, AutomatonNode node) {
        this.time = time;
        this.node = node;
    }

    public Quantity<Time> getTime() {
        return time;
    }

    public AutomatonNode getNode() {
        return node;
    }

}
