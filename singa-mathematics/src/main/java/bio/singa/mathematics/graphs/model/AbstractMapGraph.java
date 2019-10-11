package bio.singa.mathematics.graphs.model;


import bio.singa.mathematics.vectors.Vector;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is a simple implementation of the graph interface, that handles the most common operations defined for adding
 * and removing edges as well as nodes. Nodes and edge are referenced in a HashMap with integer keys and can therefor
 * quickly be retrieved and inserted.
 *
 * @param <NodeType> The type of the nodes in the graph.
 * @param <EdgeType> The type of the edges in the graph.
 * @param <VectorType> The vector that is used to define the position of this node.
 * @author cl
 */
public abstract class AbstractMapGraph<NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType>
        implements Graph<NodeType, EdgeType, IdentifierType> {


    /**
     * The edges of the graph.
     */
    protected final Map<Integer, EdgeType> edges;

    /**
     * The nodes of the graph.
     */
    private final Map<IdentifierType, NodeType> nodes;
    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier;

    /**
     * Creates a new graph object.
     */
    public AbstractMapGraph() {
        this(10, 10);
    }

    /**
     * Creates a new Graph object with an initial load capacity for the node and
     * edge list.
     *
     * @param nodeCapacity The initial capacity for the node list.
     * @param edgeCapacity The initial capacity for the edge list.
     */
    public AbstractMapGraph(int nodeCapacity, int edgeCapacity) {
        nodes = new HashMap<>(nodeCapacity);
        edges = new HashMap<>(edgeCapacity);
    }


    @Override
    public Collection<NodeType> getNodes() {
        return nodes.values();
    }

    @Override
    public NodeType getNode(IdentifierType identifier) {
        return nodes.get(identifier);
    }

    @Override
    public IdentifierType addNode(NodeType node) {
        nodes.put(node.getIdentifier(), node);
        return node.getIdentifier();
    }

    @Override
    public NodeType removeNode(NodeType node) {
        NodeType nodeToBeRemoved = nodes.values().stream()
                .filter(entry -> entry.equals(node))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not remove node " + node + "."));

        for (NodeType neighbor : nodeToBeRemoved.getNeighbours()) {
            neighbor.getNeighbours().remove(nodeToBeRemoved);
        }

        nodes.remove(node.getIdentifier());
        edges.entrySet().removeIf(edge -> edge.getValue().containsNode(node));
        return nodeToBeRemoved;
    }

    @Override
    public NodeType removeNode(IdentifierType identifier) {
        NodeType nodeToBeRemoved = nodes.values().stream()
                .filter(entry -> entry.getIdentifier().equals(identifier))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not remove node with identifier" + identifier + "."));

        for (NodeType neighbor : nodeToBeRemoved.getNeighbours()) {
            neighbor.getNeighbours().remove(nodeToBeRemoved);
        }

        nodes.remove(identifier);
        edges.entrySet().removeIf(edge -> edge.getValue().containsNode(nodeToBeRemoved));
        return nodeToBeRemoved;
    }

    @Override
    public int nextEdgeIdentifier() {
        return nextEdgeIdentifier++;
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
        return Optional.of(edge);
    }

    @Override
    public boolean containsNode(Object node) {
        return nodes.containsValue(node);
    }

    /**
     * Returns true if the graph contains any node that matches the predicate.
     *
     * @param nodePredicate The predicate to match.
     * @return The
     */
    public boolean containsNode(Predicate<NodeType> nodePredicate) {
        return nodes.values().stream()
                .anyMatch(nodePredicate);
    }

    /**
     * Evaluates the predicate for every node in the graph and returns any node that matched the predicate.
     *
     * @param nodePredicate The predicate to match.
     * @return Any node that matched the predicate and an empty optional otherwise.
     */
    public Optional<NodeType> getNode(Predicate<NodeType> nodePredicate) {
        return nodes.values().stream()
                .filter(nodePredicate)
                .findAny();
    }

    public List<NodeType> getAllNodes(Predicate<NodeType> nodePredicate) {
        return nodes.values().stream()
                .filter(nodePredicate)
                .collect(Collectors.toList());
    }

    /**
     * Evaluates the predicate for every node in the graph. An edge will be created between fhe first node that matches
     * the predicate and the given node.
     *
     * @param appendPredicate The predicate to be evaluated.
     * @param nodeToAppend The node to append.
     * @return The index of the edge created or -1 if no node matched the predicate and the node was not appended.
     */
    public int appendNode(Predicate<NodeType> appendPredicate, NodeType nodeToAppend) {
        return getNode(appendPredicate)
                .map(graphNode -> addEdgeBetween(graphNode, nodeToAppend))
                .orElse(-1);
    }

    public void appendGraph(NodeType nodeToAppendTo, NodeType nodeToAppend, Graph<NodeType, EdgeType, IdentifierType> graphToAppend) {
        Map<IdentifierType, IdentifierType> identifierMapping = new HashMap<>();
        for (NodeType node : graphToAppend.getNodes()) {
            // conserve identifier
            IdentifierType newIdentifier = nextNodeIdentifier();
            IdentifierType oldIdentifier = node.getIdentifier();
            identifierMapping.put(oldIdentifier, newIdentifier);
            // create copy
            NodeType copy = node.getCopy();
            copy.setIdentifier(newIdentifier);
            addNode(copy);
        }

        for (EdgeType edge : graphToAppend.getEdges()) {
            EdgeType edgeCopy = edge.getCopy();
            edgeCopy.setIdentifier(nextEdgeIdentifier());
            NodeType source = getNode(identifierMapping.get(edge.getSource().getIdentifier()));
            NodeType target = getNode(identifierMapping.get(edge.getTarget().getIdentifier()));
            addEdgeBetween(edgeCopy, source, target);
        }
        addEdgeBetween(nodeToAppendTo, getNode(identifierMapping.get(nodeToAppend.getIdentifier())));
    }

    /**
     * Evaluates the predicate for every node in the graph, if no node matches the predicate the given node is added.
     *
     * @param preventionPredicate The predicate to evaluate.
     * @param nodeToAdd The node to add.
     * @return The added node or the node that matched the predicate.
     */
    public NodeType addNodeIf(Predicate<NodeType> preventionPredicate, NodeType nodeToAdd) {
        return getNode(preventionPredicate)
                .orElseGet(() -> getNode(addNode(nodeToAdd)));
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

    /**
     * Returns the degree of the node with the highest degree in the graph. If no maximal degree exists, zero is
     * returned.
     *
     * @return The maximal degree of the graph.
     */
    public int getMaximumDegree() {
        return nodes.values().stream()
                .mapToInt(Node::getDegree)
                .max()
                .orElse(0);
    }

    @Override
    public String toString() {
        return "Graph [contains " + nodes.size() + " nodes and " + edges.size() + " edges]";
    }

}
