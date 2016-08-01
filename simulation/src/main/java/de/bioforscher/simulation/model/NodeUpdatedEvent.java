package de.bioforscher.simulation.model;

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
