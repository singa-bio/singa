package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.mathematics.graphs.model.DirectedWeightedEdge;

/**
 * @author cl
 */
public class DynamicProgrammingEdge extends DirectedWeightedEdge<DynamicProgrammingNode> {

    private String firstLetter;
    private String secondLetter;

    public DynamicProgrammingEdge(int identifier) {
        super(identifier);
    }

    public DynamicProgrammingEdge(int identifier, double weight) {
        super(identifier, weight);
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String getSecondLetter() {
        return secondLetter;
    }

    public void setSecondLetter(String secondLetter) {
        this.secondLetter = secondLetter;
    }
}
