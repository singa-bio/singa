package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.BioNode;

public class NodeUpdatedEvent {

    private final long epoch;
    private final BioNode node;

    public NodeUpdatedEvent(long epoch, BioNode node) {
        this.epoch = epoch;
        this.node = node;
    }

    public long getEpoch() {
        return this.epoch;
    }

    public BioNode getNode() {
        return this.node;
    }

}
