package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularGrid;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public abstract class AbstractGridGraph<NodeType extends Node<NodeType, VectorType, RectangularCoordinate>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector>
        implements Graph<NodeType, EdgeType, RectangularCoordinate> {

    /**
     * The nodes of the graph.
     */
    private final RectangularGrid<NodeType> grid;

    /**
     * The edges of the graph.
     */
    private final Map<Integer, EdgeType> edges;

    public AbstractGridGraph(int width, int height) {
        grid = new RectangularGrid<>(width, height);
        edges = new HashMap<>();
    }

    @Override
    public Collection<NodeType> getNodes() {
        return grid.getValues();
    }

    @Override
    public NodeType getNode(RectangularCoordinate identifier) {
        return grid.getValue(identifier);
    }

    public NodeType getNode(int column, int row) {
        return grid.getValue(column, row);
    }

    @Override
    public RectangularCoordinate addNode(NodeType node) {
        RectangularCoordinate coordinate = node.getIdentifier();
        grid.setValue(coordinate, node);
        return coordinate;
    }

    @Override
    public NodeType removeNode(NodeType node) {
        return removeNode(node.getIdentifier());
    }

    @Override
    public NodeType removeNode(RectangularCoordinate identifier) {
        return grid.removeValue(identifier);
    }

    @Override
    public Collection<EdgeType> getEdges() {
        return edges.values();
    }

    @Override
    public EdgeType getEdge(int identifier) {
        return edges.get(identifier);
    }

    @Override
    public int addEdgeBetween(EdgeType edge, NodeType source, NodeType target) {
        edge.setSource(source);
        edge.setTarget(target);
        edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    @Override
    public boolean containsNode(Object node) {
        return grid.containsValue(node);
    }

    @Override
    public boolean containsEdge(Object edge) {
        return edges.containsValue(edge);
    }

    @Override
    public RectangularCoordinate nextNodeIdentifier() {
        return null;
    }

}
