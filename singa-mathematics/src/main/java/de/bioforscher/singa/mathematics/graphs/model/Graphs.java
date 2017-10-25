package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.algorithms.graphs.DisconnectedSubgraphFinder;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTree;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTreeNode;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * A factory class used to create graphs and convert other things to graphs.
 *
 * @author cl
 */
public class Graphs {

    private static final Logger logger = LoggerFactory.getLogger(Graphs.class);

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
            graph.addNode(Nodes.createRandomlyPlacedNode(i, boundingBox));
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
        RegularNode root = Nodes.createRandomlyPlacedNode(0, boundingBox);
        graph.addNode(root);
        Graphs.growTree(depth - 1, graph, root, boundingBox);
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
        graph.addNode(Nodes.createRandomlyPlacedNode(next, boundingBox));
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
            graph.addNode(Nodes.createRandomlyPlacedNode(i, boundingBox));
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

    public static UndirectedGraph buildGridGraph(int columns, int rows, Rectangle boundingBox) {
        return buildGridGraph(columns, rows, boundingBox, false);
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
        logger.debug("Creating grid graph ...");
        UndirectedGraph graph = new UndirectedGraph();
        double horizontalSpacing = boundingBox.getWidth() / (rows + 1);
        double verticalSpacing = boundingBox.getHeight() / (columns + 1);

        // adding nodes
        logger.debug("Creating and placing nodes ...");
        int nodeCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode node = new RegularNode(nodeCounter);
                node.setPosition(new Vector2D(horizontalSpacing * (column + 1), verticalSpacing * (row + 1)));
                graph.addNode(node);
                nodeCounter++;
            }
        }

        Collection<RegularNode> nodes = graph.getNodes();

        // horizontal connections
        logger.debug("Adding horizontal connections ...");
        int horizontalCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode source = graph.getNode(horizontalCounter);
                RegularNode target = graph.getNode(horizontalCounter + 1);
                if (horizontalCounter < nodes.size() - 1) {
                    if (horizontalCounter % rows != rows - 1) {
                        graph.addEdgeBetween(horizontalCounter, source, target);
                    }
                }
                horizontalCounter++;
            }
        }

        // vertical connections
        logger.debug("Adding vertical connections ...");
        int verticalCounter = 0;
        for (int row = 0; row < columns; row++) {
            for (int column = 0; column < rows; column++) {
                RegularNode source = graph.getNode(verticalCounter);
                RegularNode target = graph.getNode(verticalCounter + rows);
                if (verticalCounter + rows < nodes.size()) {
                    graph.addEdgeBetween(horizontalCounter + verticalCounter + 1, source, target);
                }
                verticalCounter++;
            }
        }

        // periodic border conditions
        if (periodic) {
            logger.debug("Adding periodic boundary connections");
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

    /**
     * Converts a {@link BinaryTree} to a {@link GenericGraph}.
     *
     * @param tree The tree.
     * @param <ContentType> The content of the resulting generic graph.
     * @return The generic graph.
     */
    public static <ContentType> GenericGraph<ContentType> convertTreeToGraph(BinaryTree<ContentType> tree) {
        GenericGraph<ContentType> graph = new GenericGraph<>();
        BinaryTreeNode<ContentType> root = tree.getRoot();
        GenericNode<ContentType> rootNode = convertNode(root, graph);
        graph.addNode(rootNode);
        traverseNode(graph, rootNode, root.getLeft());
        traverseNode(graph, rootNode, root.getRight());
        return graph;
    }

    /**
     * Converts a {@link BinaryTreeNode} to a {@link GenericNode} and connects it in the given graph to the source node.
     * Recursively traverses all child nodes.
     *
     * @param graph The graph where the node is added.
     * @param source The node to connect to.
     * @param treeNode The node to convert.
     * @param <ContentType> The content type of the node.
     */
    private static <ContentType> void traverseNode(GenericGraph<ContentType> graph,
                                                   GenericNode<ContentType> source, BinaryTreeNode<ContentType> treeNode) {
        // add current tree to the graph and connect it
        GenericNode<ContentType> graphNode = convertNode(treeNode, graph);
        graph.addNode(graphNode);
        graph.addEdgeBetween(source, graphNode);
        // traverse the children
        if (treeNode.getLeft() != null) {
            traverseNode(graph, graphNode, treeNode.getLeft());
        }
        if (treeNode.getRight() != null) {
            traverseNode(graph, graphNode, treeNode.getRight());
        }
    }

    /**
     * Converts a {@link BinaryTreeNode} to a {@link GenericNode} with the next free node identifer from the graph.
     * Places the node randomly in a 200 x 200 rectangle.
     *
     * @param treeNode The node to convert.
     * @param graph The graph to take the index from.
     * @param <ContentType> The content type of the node.
     * @return The converted {@link GenericNode}.
     */
    private static <ContentType> GenericNode<ContentType> convertNode(BinaryTreeNode<ContentType> treeNode,
                                                                      GenericGraph<ContentType> graph) {
        GenericNode<ContentType> result = new GenericNode<>(graph.nextNodeIdentifier(), treeNode.getData());
        result.setPosition(Vectors.generateRandom2DVector(new Rectangle(200, 200)));
        return result;
    }

    /**
     * Given a graph, this method returns a list of all disconnected subgraphs. The subgraphs are copies and changes are
     * not reflected back into the original graph, but node and edge identifiers, as well as attached data is conserved.
     *
     * @param graph The graph to decompose.
     * @param <NodeType> The type of the nodes.
     * @param <EdgeType> The type of the edges.
     * @param <GraphType> The type of the graph.
     * @param <VectorType> The position type.
     * @param <IdentifierType> The type of the identifier.
     * @return A list of all disconnected subgraphs.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> List<GraphType> findDisconnectedSubgraphs(GraphType graph) {
        return DisconnectedSubgraphFinder.findDisconnectedSubgraphs(graph);
    }


}
