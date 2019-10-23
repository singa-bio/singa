package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author cl
 */
public abstract class BinaryForce<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends Force<NodeType, EdgeType, IdentifierType, GraphType> {

    private BiPredicate<NodeType, NodeType> forcePredicate;

    public BinaryForce(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout) {
        super(parentLayout);
    }

    public BiPredicate<NodeType, NodeType> getForcePredicate() {
        return forcePredicate;
    }

    public void setForcePredicate(BiPredicate<NodeType, NodeType> forcePredicate) {
        this.forcePredicate = forcePredicate;
    }

    @Override
    public void apply(GraphType graph) {
        List<NodeType> nodes = new ArrayList<>(graph.getNodes());
        int nodeNumber = nodes.size();
        for (int i = 0; i < nodeNumber; i++) {
            for (int j = 0; j < i; j++) {
                NodeType first = nodes.get(i);
                NodeType second = nodes.get(j);
                if (getForcePredicate().test(first, second)) {
                    determineDisplacement(first, second);
                }
            }
        }
    }

    abstract void determineDisplacement(NodeType first, NodeType second);

}
