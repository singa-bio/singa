package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.graphs.model.DirectedWeightedGraph;
import de.bioforscher.singa.sequence.model.ProteinSequence;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;

/**
 * @author cl
 */
public class DynamicProgrammingGraph extends DirectedWeightedGraph<DynamicProgrammingNode, DynamicProgrammingEdge> {

    private ProteinSequence firstSequence;
    private ProteinSequence secondSequence;

    private DynamicProgrammingNode[][] nodes;

    public DynamicProgrammingGraph(ProteinSequence firstSequence, ProteinSequence secondSequence) {
        this.firstSequence = firstSequence;
        this. secondSequence = secondSequence;
    }

    public Integer addNode(DynamicProgrammingNode node, int firstIndex, int secondIndex) {
        nodes[firstIndex][secondIndex] = node;
        return super.addNode(node);
    }

    public DynamicProgrammingNode getNode(int firstIndex, int secondIndex) {
        return nodes[firstIndex][secondIndex];
    }

    public Pair<AminoAcidFamily> getParing(int firstIndex, int secondIndex) {
        return new Pair<>(firstSequence.getLetter(firstIndex), secondSequence.getLetter(secondIndex));
    }

    public ProteinSequence getFirstSequence() {
        return firstSequence;
    }

    public ProteinSequence getSecondSequence() {
        return secondSequence;
    }

    @Override
    public int addEdgeBetween(int identifier, DynamicProgrammingNode source, DynamicProgrammingNode target) {
        return addEdgeBetween(new DynamicProgrammingEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(DynamicProgrammingNode source, DynamicProgrammingNode target) {
        return addEdgeBetween(nextNodeIdentifier(), source, target);
    }

    public int addEdgeBetween(int identifier, double weight, DynamicProgrammingNode source, DynamicProgrammingNode target) {
        return addEdgeBetween(new DynamicProgrammingEdge(identifier, weight), source, target);
    }

    public int addEdgeBetween(DynamicProgrammingNode source, DynamicProgrammingNode target, double weight) {
        return addEdgeBetween(nextNodeIdentifier(), weight, source, target);
    }




}
