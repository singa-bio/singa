package de.bioforscher.simulation.model;

public class NextEpochEvent {

    private final int epoch;
    private final BioNode node;

    public NextEpochEvent(int epoch, BioNode node) {
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
