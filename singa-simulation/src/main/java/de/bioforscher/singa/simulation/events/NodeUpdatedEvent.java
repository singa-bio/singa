package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.BioNode;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

public class NodeUpdatedEvent {

    private final Quantity<Time> time;
    private final BioNode node;

    public NodeUpdatedEvent(Quantity<Time> time, BioNode node) {
        this.time = time;
        this.node = node;
    }

    public Quantity<Time> getTime() {
        return this.time;
    }

    public BioNode getNode() {
        return this.node;
    }

}
