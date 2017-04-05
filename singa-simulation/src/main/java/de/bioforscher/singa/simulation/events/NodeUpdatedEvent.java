package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.model.graphs.BioNode;

public class NodeUpdatedEvent {

    private final int epoch;
    private final BioNode node;

    public NodeUpdatedEvent(int epoch, BioNode node) {
        this.epoch = epoch;
        this.node = node;
    }

    public int getEpoch() {
        return this.epoch;
    }

    public BioNode getNode() {
        return this.node;
    }

}
