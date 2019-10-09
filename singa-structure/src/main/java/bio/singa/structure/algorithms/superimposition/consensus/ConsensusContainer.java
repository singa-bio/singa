package bio.singa.structure.algorithms.superimposition.consensus;

import bio.singa.mathematics.graphs.trees.BinaryTree;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.model.oak.StructuralMotif;

/**
 * A container encapsulating a {@link StructuralMotif} with a {@link BinaryTree} that represents its associated
 * consensus tree.
 *
 * @author fk
 */
public class ConsensusContainer {

    private final StructuralMotif structuralMotif;
    private final boolean consensus;
    private double consensusDistance;
    private BinaryTree<ConsensusContainer> consensusTree;
    private SubstructureSuperimposition superimposition;

    public ConsensusContainer(StructuralMotif structuralMotif, boolean consensus) {
        this.structuralMotif = structuralMotif;
        this.consensus = consensus;
        consensusDistance = 0.0;
    }

    /**
     * Returns true if the associated {@link StructuralMotif} is an artificial consensus.
     *
     * @return True if consensus.
     */
    public boolean isConsensus() {
        return consensus;
    }

    public SubstructureSuperimposition getSuperimposition() {
        return superimposition;
    }

    public void setSuperimposition(SubstructureSuperimposition superimposition) {
        this.superimposition = superimposition;
    }

    @Override
    public String toString() {
        return structuralMotif.toString() +
                "_" + consensusDistance;
    }

    public StructuralMotif getStructuralMotif() {
        return structuralMotif;
    }



    public BinaryTree<ConsensusContainer> getConsensusTree() {
        return consensusTree;
    }

    public void setConsensusTree(BinaryTree<ConsensusContainer> consensusTree) {
        this.consensusTree = consensusTree;
    }

    public void addToConsensusDistance(double delta) {
        consensusDistance += delta;
    }

    public double getConsensusDistance() {
        return consensusDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsensusContainer that = (ConsensusContainer) o;

        return structuralMotif != null ? structuralMotif.equals(that.structuralMotif) : that.structuralMotif == null;
    }

    @Override
    public int hashCode() {
        return structuralMotif != null ? structuralMotif.hashCode() : 0;
    }
}
