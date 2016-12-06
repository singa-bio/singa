package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

import java.util.List;

/**
 * @author fk
 */
public class ConsensusContainer {

    private final List<LeafSubstructure<?, ?>> leafSubstructures;
    private double consensusDistance;

    public ConsensusContainer(List<LeafSubstructure<?, ?>> leafSubstructures) {
        this.leafSubstructures = leafSubstructures;
        this.consensusDistance = 0.0;
    }

    public void addToConsensusDistance(double delta) {
        this.consensusDistance += delta;
    }

    public double getConsensusDistance() {
        return this.consensusDistance;
    }
}
