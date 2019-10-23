package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class UnaryForce<NodeType extends Node<NodeType, Vector2D, ?>> extends Force<NodeType> {

    private Predicate<NodeType> forcePredicate;

    public UnaryForce(ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout) {
        super(parentLayout);
    }

    public Predicate<NodeType> getForcePredicate() {
        return forcePredicate;
    }

    public void setForcePredicate(Predicate<NodeType> forcePredicate) {
        this.forcePredicate = forcePredicate;
    }

    @Override
    public void apply(Graph<NodeType, ?, ?> graph) {
        graph.getNodes().stream()
                .filter(forcePredicate)
                .forEach(this::determineDisplacement);
    }

    abstract void determineDisplacement(NodeType node);

}
