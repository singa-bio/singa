package de.bioforscher.singa.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTree;

/**
 * A container encapsulating a {@link StructuralMotif} with a {@link BinaryTree} that represents its associated
 * consensus tree.
 *
 * @author fk
 */
public class ConsensusContainer {

    private StructuralMotif structuralMotif;
    private double consensusDistance;
    private BinaryTree<ConsensusContainer> consensusTree;
    private SubstructureSuperimposition superimposition;

    public ConsensusContainer(StructuralMotif structuralMotif) {
        this.structuralMotif = structuralMotif;
        this.consensusDistance = 0.0;
    }

    public SubstructureSuperimposition getSuperimposition() {
        return this.superimposition;
    }

    public void setSuperimposition(SubstructureSuperimposition superimposition) {
        this.superimposition = superimposition;
    }

    @Override
    public String toString() {
        return this.structuralMotif.toString() +
                ":" + this.consensusDistance;
    }

    public StructuralMotif getStructuralMotif() {
        return this.structuralMotif;
    }

    public BinaryTree<ConsensusContainer> getConsensusTree() {
        return this.consensusTree;
    }

    public void setConsensusTree(BinaryTree<ConsensusContainer> consensusTree) {
        this.consensusTree = consensusTree;
    }

    public void addToConsensusDistance(double delta) {
        this.consensusDistance += delta;
    }

    public double getConsensusDistance() {
        return this.consensusDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsensusContainer that = (ConsensusContainer) o;

        return this.structuralMotif != null ? this.structuralMotif.equals(that.structuralMotif) : that.structuralMotif == null;
    }

    @Override
    public int hashCode() {
        return this.structuralMotif != null ? this.structuralMotif.hashCode() : 0;
    }
}
