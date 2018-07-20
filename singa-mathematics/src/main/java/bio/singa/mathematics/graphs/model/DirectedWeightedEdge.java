package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector;

/**
 * @author cl
 */
public class DirectedWeightedEdge<NodeType extends Node<NodeType, ? extends Vector, ?>> extends AbstractEdge<NodeType> implements Weighted<Double> {

    private double weight;

    public DirectedWeightedEdge(int identifier) {
        super(identifier);
    }

    public DirectedWeightedEdge(int identifier, double weight) {
        super(identifier);
        this.weight = weight;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
