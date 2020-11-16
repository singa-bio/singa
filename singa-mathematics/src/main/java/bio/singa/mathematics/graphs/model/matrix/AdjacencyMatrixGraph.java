package bio.singa.mathematics.graphs.model.matrix;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public abstract class AdjacencyMatrixGraph<NodeType extends Node<NodeType, VectorType, Integer>,
        EdgeType extends Edge<NodeType>,
        VectorType extends Vector>
        implements Graph<NodeType, EdgeType, Integer> {

    private List<NodeType> nodes;
    private HashMap<Integer, EdgeType> edges;
    private final int[][] adjacancyMatrix;

    public AdjacencyMatrixGraph(List<NodeType> nodes) {
        adjacancyMatrix = new int[nodes.size()][nodes.size()];
        this.nodes = new ArrayList<>(nodes);
        edges = new HashMap<>();
    }

    @Override
    public Collection<NodeType> getNodes() {
        return nodes;
    }

    @Override
    public NodeType getNode(Integer identifier) {
        return nodes.get(identifier);
    }

    @Override
    public Integer addNode(NodeType node) {
        throw new UnsupportedOperationException("AdjacencyMatrixGraphs are fixed size graphs and no nodes can be added " +
                "or removed after initialization");
    }

    @Override
    public NodeType removeNode(NodeType node) {
        throw new UnsupportedOperationException("AdjacencyMatrixGraphs are fixed size graphs and no nodes can be added " +
                "or removed after initialization");
    }

    @Override
    public NodeType removeNode(Integer identifier) {
        throw new UnsupportedOperationException("AdjacencyMatrixGraphs are fixed size graphs and no nodes can be added " +
                "or removed after initialization");
    }

    @Override
    public Collection<EdgeType> getEdges() {
        return edges.values();
    }

    @Override
    public EdgeType getEdge(int identifier) {
        return edges.get(identifier);
    }

    /**
     * Adds a new edge to the graph, connecting source and target nodes. This method also references source and target
     * as neighbors to each other.
     *
     * @param edge The edge to be added.
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    public int addEdgeBetween(EdgeType edge, NodeType source, NodeType target) {
        edge.setSource(source);
        edge.setTarget(target);
        edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        adjacancyMatrix[source.getIdentifier()][target.getIdentifier()] = edge.getIdentifier();
        adjacancyMatrix[target.getIdentifier()][source.getIdentifier()] = edge.getIdentifier();
        return edge.getIdentifier();
    }

    /**
     * Adds a new edge with the given identifier to the graph, connecting source and target nodes. This method also
     * references source and target as neighbors to each other.
     *
     * @param identifier The edge identifier.
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    public abstract int addEdgeBetween(int identifier, NodeType source, NodeType target);

    /**
     * Adds a new edge with the next free identifier to the graph, connecting source and target nodes. This method also
     * references source and target as neighbors to each other.
     *
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    public abstract int addEdgeBetween(NodeType source, NodeType target);

    /**
     * Returns the any edge between both nodes. The ordering (source, target) that may be defined does not matter.
     *
     * @param first The first node.
     * @param second The second node.
     * @return Any edge that connects both nodes, or an empty node otherwise.
     */
    public Optional<EdgeType> getEdgeBetween(NodeType first, NodeType second) {
        Integer firstIdentifier = first.getIdentifier();
        Integer secondIdentifier = second.getIdentifier();
        int edgeIdentifier = adjacancyMatrix[firstIdentifier][secondIdentifier];
        return Optional.ofNullable(edges.get(edgeIdentifier));
    }

    /**
     * Returns all nodes touching the given node (i.e. neighbours regardless of the eventual directionality of the
     * connecting edge). Prefer {@link Node#getNeighbours()} to this method if possible, since this has higher
     * complexity.
     * @param node The node.
     * @return The neighbours.
     */
    public List<NodeType> getTouchingNodes(NodeType node) {
        int[] adjacetEdges = adjacancyMatrix[node.getIdentifier()];
        return Arrays.stream(adjacetEdges)
                .boxed()
                .map(nodes::get)
                .collect(Collectors.toList());
    }

    /**
     * Remove the edge from the graph. Also removes corresponding neighbouring node relations.
     *
     * @param source The source node.
     * @param target The target node.
     * @return The edge that was removed or an empty optional if no edge could be found between the nodes.
     */
    public Optional<EdgeType> removeEdge(NodeType source, NodeType target) {
        Optional<EdgeType> optionalEdge = getEdgeBetween(source, target);
        if (optionalEdge.isPresent()) {
            EdgeType edge = optionalEdge.get();
            removeEdge(edge);
        }
        return Optional.empty();
    }

    public Optional<EdgeType> removeEdge(EdgeType edge) {
        edges.remove(edge.getIdentifier());
        edge.getSource().getNeighbours().remove(edge.getTarget());
        edge.getTarget().getNeighbours().remove(edge.getSource());
        adjacancyMatrix[edge.getSource().getIdentifier()][edge.getTarget().getIdentifier()] = 0;
        adjacancyMatrix[edge.getTarget().getIdentifier()][edge.getSource().getIdentifier()] = 0;
        return Optional.of(edge);
    }

    @Override
    public boolean containsNode(Object node) {
        return nodes.contains(node);
    }

    /**
     * Returns true if the graph contains any node that matches the predicate.
     *
     * @param nodePredicate The predicate to match.
     * @return The
     */
    public boolean containsNode(Predicate<NodeType> nodePredicate) {
        return nodes.stream()
                .anyMatch(nodePredicate);
    }

    /**
     * Evaluates the predicate for every node in the graph and returns any node that matched the predicate.
     *
     * @param nodePredicate The predicate to match.
     * @return Any node that matched the predicate and an empty optional otherwise.
     */
    public Optional<NodeType> getNode(Predicate<NodeType> nodePredicate) {
        return nodes.stream()
                .filter(nodePredicate)
                .findAny();
    }

    public List<NodeType> getAllNodes(Predicate<NodeType> nodePredicate) {
        return nodes.stream()
                .filter(nodePredicate)
                .collect(Collectors.toList());
    }

    public boolean containsEdge(int edge) {
        return edges.containsKey(edge);
    }

    @Override
    public boolean containsEdge(Object edge) {
        return edges.containsValue(edge);
    }

    public boolean containsEdge(Predicate<EdgeType> edgePredicate) {
        return edges.values().stream()
                .anyMatch(edgePredicate);
    }

    public Optional<EdgeType> getEdge(Predicate<EdgeType> edgePredicate) {
        return edges.values().stream()
                .filter(edgePredicate)
                .findAny();
    }

    @Override
    public String toString() {
        return "Graph [contains " + nodes.size() + " nodes and " + edges.size() + " edges]";
    }

}
