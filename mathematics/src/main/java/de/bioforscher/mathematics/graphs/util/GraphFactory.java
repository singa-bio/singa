package de.bioforscher.mathematics.graphs.util;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.mathematics.sequences.Sequences;
import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * A factory class used to create some Graphs.
 *
 * @author cl
 */
public class GraphFactory {

    /**
     * Generates a linear graph with the given number of nodes. Each node will be connected to its predecessor.
     *
     * @param numberOfNodes The number of nodes the graph should contain.
     * @param boundingBox A bounding box where the nodes should be positioned.
     * @return A linear Graph
     */
    public static UndirectedGraph buildLinearGraph(int numberOfNodes, Rectangle boundingBox) {
        UndirectedGraph graph = new UndirectedGraph();
        for (int i = 0; i < numberOfNodes; i++) {
            graph.addNode(NodeFactory.createRandomlyPlacedNode(i, boundingBox));
        }
        for (int i = 0; i < numberOfNodes - 1; i++) {
            graph.addEdgeBetween(i, graph.getNode(i), graph.getNode(i + 1));
        }
        return graph;
    }

    /**
     * Generates a circular Graph with the given number of nodes. Each node will be connected to its predecessor and
     * successor.
     *
     * @param numberOfNodes The number of nodes the circle should contain.
     * @param boundingBox A bounding box where the nodes should be positioned.
     * @return A circular graph.
     */
    public static UndirectedGraph buildCircularGraph(int numberOfNodes, Rectangle boundingBox) {
        UndirectedGraph graph = buildLinearGraph(numberOfNodes, boundingBox);
        graph.addEdgeBetween(numberOfNodes, graph.getNode(numberOfNodes - 1), graph.getNode(0));
        return graph;
    }

    /**
     * Generates a graph with a tree-like structure, where every node is connected to one predecessor and two
     * successors, thus forming a fractal structure.
     *
     * @param depth The depth of the tree.
     * @param boundingBox A bounding box where the nodes should be positioned.
     * @return A tree-like graph.
     */
    public static UndirectedGraph buildTreeGraph(int depth, Rectangle boundingBox) {
        if (depth < 1) {
            throw new IllegalArgumentException("The depth of a tree-like graph must be at least 1");
        }
        UndirectedGraph graph = new UndirectedGraph();
        RegularNode root = NodeFactory.createRandomlyPlacedNode(0, boundingBox);
        graph.addNode(root);
        GraphFactory.growTree(depth - 1, graph, root, boundingBox);
        return graph;
    }

    /**
     * A private method used to grow the tree-like graph structure. The given graph will be modified!
     *
     * @param depth The current depth.
     * @param graph The graph to add the new node.
     * @param predecessor The previously added node.
     * @param boundingBox A bounding box where the nodes should be positioned.
     */
    private static void growTree(int depth, UndirectedGraph graph, RegularNode predecessor, Rectangle boundingBox) {
        int next = graph.nextNodeIdentifier();
        graph.addNode(NodeFactory.createRandomlyPlacedNode(next, boundingBox));
        graph.addEdgeBetween(graph.nextEdgeIdentifier(), predecessor, graph.getNode(next));
        if (depth > 0) {
            growTree(depth - 1, graph, graph.getNode(next), boundingBox);
            growTree(depth - 1, graph, graph.getNode(next), boundingBox);
        }
    }

    /**
     * Generates a randomised graph based on the Erdös - Renyi model.
     *
     * @param numberOfNodes The number of nodes the graph should contain.
     * @param edgeProbability The probability, that two nodes will be connected.
     * @param boundingBox A bounding box where the nodes should be positioned.
     * @return A randomized graph.
     */
    public static UndirectedGraph buildRandomGraph(int numberOfNodes, double edgeProbability, Rectangle boundingBox) {
        UndirectedGraph graph = new UndirectedGraph();
        for (int i = 0; i < numberOfNodes; i++) {
            graph.addNode(NodeFactory.createRandomlyPlacedNode(i, boundingBox));
        }
        int j = 0;
        for (RegularNode source : graph.getNodes()) {
            for (RegularNode target : graph.getNodes()) {
                if (!source.equals(target)) {
                    if (Math.random() < edgeProbability) {
                        graph.addEdgeBetween(j, source, target);
                        j++;
                    }
                }
            }
        }
        return graph;
    }

    /**
     * Generates a grid graph with columns and rows.
     *
     * @param boundingBox Rectangle where the Graph is positioned.
     * @param columns The Number of columns
     * @param rows The Number of rows
     * @param periodic Applies periodic boundary condition, if {@code true}.
     * @return A rectangular grid graph.
     */
    public static UndirectedGraph buildGridGraph(int columns, int rows, Rectangle boundingBox, boolean periodic) {
        UndirectedGraph graph = new UndirectedGraph();
        double horizontalSpacing = boundingBox.getWidth() / (rows + 1);
        double verticalSpacing = boundingBox.getHeight() / (columns + 1);
        // adding nodes
        int nodeCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode node = new RegularNode(nodeCounter);
                node.setPosition(new Vector2D(horizontalSpacing * (column + 1), verticalSpacing * (row + 1)));
                graph.addNode(node);
                nodeCounter++;
            }
        }

        // horizontal connections
        int horizontalCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode source = graph.getNode(horizontalCounter);
                RegularNode target = graph.getNode(horizontalCounter + 1);
                if (horizontalCounter < graph.getNodes().size() - 1) {
                    if (horizontalCounter % rows != rows - 1) {
                        graph.addEdgeBetween(horizontalCounter, source, target);
                    }
                }
                horizontalCounter++;
            }
        }

        // vertical connections
        int verticalCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode source = graph.getNode(verticalCounter);
                RegularNode target = graph.getNode(verticalCounter + rows);
                if (verticalCounter + rows < graph.getNodes().size()) {
                    graph.addEdgeBetween(horizontalCounter + verticalCounter + 1, source, target);
                }
                verticalCounter++;
            }
        }

        // periodic border conditions
        if (periodic) {

            // horizontal connections
            for (int c = 0; c < rows; c++) {
                RegularNode source = graph.getNode(c);
                RegularNode target = graph.getNode(graph.getNodes().size() - (rows - c));
                graph.addEdgeBetween(horizontalCounter + verticalCounter + c + 1, source, target);
            }

            // vertical connections
            for (int r = 0; r < columns; r++) {
                RegularNode source = graph.getNode(r * columns);
                RegularNode target = graph.getNode(r * columns + columns - 1);
                graph.addEdgeBetween(horizontalCounter + verticalCounter + rows + r + 1, source, target);
            }

        }

        return graph;
    }

}
