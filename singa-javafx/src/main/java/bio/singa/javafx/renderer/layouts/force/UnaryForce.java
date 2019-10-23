package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class UnaryForce<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends Force<NodeType, EdgeType, IdentifierType, GraphType> {

    private Predicate<NodeType> forcePredicate;

    public UnaryForce(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout) {
        super(parentLayout);
    }

    public Predicate<NodeType> getForcePredicate() {
        return forcePredicate;
    }

    public void setForcePredicate(Predicate<NodeType> forcePredicate) {
        this.forcePredicate = forcePredicate;
    }

    @Override
    public void apply(GraphType graph) {
        graph.getNodes().stream()
                .filter(forcePredicate)
                .forEach(this::determineDisplacement);
    }

    abstract void determineDisplacement(NodeType node);

}
