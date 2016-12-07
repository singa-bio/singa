package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.mathematics.graphs.trees.BinaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class ConsensusContainer {

    private List<LeafSubstructure<?, ?>> leafSubstructures;
    private double consensusDistance;
    private BinaryTree<ConsensusContainer> consensusTree;
    public ConsensusContainer(List<LeafSubstructure<?, ?>> leafSubstructures) {
        this.leafSubstructures = leafSubstructures;
        this.consensusDistance = 0.0;
    }

    public ConsensusContainer() {
        this(new ArrayList<>());
    }

    @Override
    public String toString() {
        return this.leafSubstructures.stream()
                .map(leafSubstructure -> leafSubstructure.getFamily().getOneLetterCode() + "-"
                        + leafSubstructure.getIdentifier())
                .collect(Collectors.joining("_")) + ":" + this.consensusDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsensusContainer that = (ConsensusContainer) o;

        return this.leafSubstructures != null ? this.leafSubstructures.equals(that.leafSubstructures) : that.leafSubstructures == null;
    }

    @Override
    public int hashCode() {
        return this.leafSubstructures != null ? this.leafSubstructures.hashCode() : 0;
    }

    public BinaryTree<ConsensusContainer> getConsensusTree() {
        return this.consensusTree;
    }

    public void setConsensusTree(BinaryTree<ConsensusContainer> consensusTree) {
        this.consensusTree = consensusTree;
    }

    public void addLeaveStructure(LeafSubstructure<?, ?> leafSubstructure) {
        this.leafSubstructures.add(leafSubstructure);
    }

    public void addToConsensusDistance(double delta) {
        this.consensusDistance += delta;
    }

    public double getConsensusDistance() {
        return this.consensusDistance;
    }

    public List<LeafSubstructure<?, ?>> getLeafSubstructures() {
        return this.leafSubstructures;
    }

    public void setLeafSubstructures(List<LeafSubstructure<?, ?>> leafSubstructures) {
        this.leafSubstructures = leafSubstructures;
    }
}
