package bio.singa.sequence.algorithms.alignment;

import bio.singa.mathematics.graphs.model.DirectedWeightedEdge;
import bio.singa.structure.model.families.AminoAcidFamily;

/**
 * @author cl
 */
public class DynamicProgrammingEdge extends DirectedWeightedEdge<DynamicProgrammingNode> {

    private AminoAcidFamily first;
    private AminoAcidFamily second;

    public DynamicProgrammingEdge(int identifier) {
        super(identifier);
    }

    public DynamicProgrammingEdge(int identifier, double weight) {
        super(identifier, weight);
    }

    public DynamicProgrammingEdge(int identifier, double weight, AminoAcidFamily first, AminoAcidFamily second) {
        super(identifier, weight);
        this.first = first;
        this.second = second;
    }

    public AminoAcidFamily getFirst() {
        return first;
    }

    public void setFirst(AminoAcidFamily first) {
        this.first = first;
    }

    public AminoAcidFamily getSecond() {
        return second;
    }

    public void setSecond(AminoAcidFamily second) {
        this.second = second;
    }
}
