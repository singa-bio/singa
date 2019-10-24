package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A runnable to center a 2D graph when drawn.
 *
 * @param <NodeType>       The node type of the graph.
 * @param <EdgeType>       The edge type of the graph.
 * @param <IdentifierType> The identifier type of the graph.
 * @param <GraphType>      The type of the graph.
 */
public class GraphAligner<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements Runnable {

    private final GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> graphRenderer;
    private final GraphType graph;
    private final Predicate<NodeType> nodePredicate;

    public GraphAligner(GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> graphRenderer, GraphType graph, Predicate<NodeType> nodePredicate) {
        this.graphRenderer = graphRenderer;
        this.graph = graph;
        this.nodePredicate = nodePredicate;
    }

    @Override
    public void run() {
        graphRenderer.getGraphQueue().add(centerGraph());
    }

    public GraphType centerGraph() {
        double drawingWidth = graphRenderer.getDrawingWidth();
        double drawingHeight = graphRenderer.getDrawingHeight();
        Vector2D referenceCentroid = new Vector2D(drawingWidth, drawingHeight).divide(2.0);
        Vector nodeCentroid = Vectors.getCentroid(graph.getNodes().stream()
                .filter(nodePredicate)
                .map(Node::getPosition)
                .collect(Collectors.toList()));
        Vector2D shiftVector = referenceCentroid.subtract(nodeCentroid).as(Vector2D.class);
        for (NodeType node : graph.getNodes()) {
            node.setPosition(node.getPosition().add(shiftVector));
        }
        return graph;
    }
}
