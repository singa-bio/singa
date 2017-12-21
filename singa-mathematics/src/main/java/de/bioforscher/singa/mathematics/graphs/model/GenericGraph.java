package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.Collection;
import java.util.Optional;

/**
 * The generic graph class connects generic nodes with generic edges. Generic in this sense means, that any content can
 * be associated to the nodes, connected by the edges.
 *
 * @param <ContentType> The content type of the nodes.
 * @author cl
 */
public class GenericGraph<ContentType> extends AbstractGraph<GenericNode<ContentType>, GenericEdge<ContentType>, Vector2D, Integer> {

    /**
     * A iterating variable to add a new node.
     */
    private int nextNodeIdentifier;

    @Override
    public int addEdgeBetween(int identifier, GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(new GenericEdge<>(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    /**
     * Checks whether nodes containing the source or target content are already contained in the graphs. Adds new nodes
     * for the content that is not found the the graph and connects the previously created or retrieved nodes.
     *
     * @param sourceContent The content of the source node.
     * @param targetContent The content of the target node.
     * @return The identifier of the generated edge.
     */
    public int addEdgeBetween(ContentType sourceContent, ContentType targetContent) {
        Optional<GenericNode<ContentType>> optionalSourceNode = getNodeWithContent(sourceContent);
        Optional<GenericNode<ContentType>> optionalTargetNode = getNodeWithContent(targetContent);
        // check if nodes for content are already present
        if (optionalSourceNode.isPresent()) {
            if (optionalTargetNode.isPresent()) {
                // both nodes already present
                return addEdgeBetween(optionalSourceNode.get(), optionalTargetNode.get());
            } else {
                // source is present but not target
                GenericNode<ContentType> targetNode = addNode(targetContent);
                return addEdgeBetween(optionalSourceNode.get(), targetNode);
            }
        } else {
            if (optionalTargetNode.isPresent()) {
                // target is present but not source
                GenericNode<ContentType> sourceNode = addNode(sourceContent);
                return addEdgeBetween(optionalTargetNode.get(), sourceNode);
            } else {
                // neither is present
                GenericNode<ContentType> targetNode = addNode(targetContent);
                GenericNode<ContentType> sourceNode = addNode(sourceContent);
                return addEdgeBetween(targetNode, sourceNode);
            }
        }
    }

    @Override
    public Integer nextNodeIdentifier() {
        return nextNodeIdentifier++;
    }

    /**
     * Adds a new node with the given content to the graph.
     * @param content The content of the new node.
     * @return The node that was added.
     */
    public GenericNode<ContentType> addNode(ContentType content) {
        final GenericNode<ContentType> genericNode = new GenericNode<>(nextNodeIdentifier(), content);
        addNode(genericNode);
        return genericNode;
    }

    /**
     * Returns true if a node with the given content is already present.
     * @param content The content to check.
     * @return true if a node with the given content is already present.
     */
    public boolean containsNodeWithContent(ContentType content) {
        for (GenericNode<ContentType> genericNode : getNodes()) {
            if (genericNode.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the node with the given content or an empty optional if no node could be found.
     * @param content The content of the node.
     * @return The node with the given content or an empty optional if no node could be found.
     */
    public Optional<GenericNode<ContentType>> getNodeWithContent(ContentType content) {
        for (GenericNode<ContentType> genericNode : getNodes()) {
            if (genericNode.getContent().equals(content)) {
                return Optional.of(genericNode);
            }
        }
        return Optional.empty();
    }

    public GenericGraph<ContentType> getCopy() {
        // create a new graph
       GenericGraph<ContentType> graphCopy = new GenericGraph<>();
        // copy and add nodes
        Collection<GenericNode<ContentType>> nodes = getNodes();
        for (GenericNode<ContentType> node : nodes) {
            // remember copy does not copy neighbours
            GenericNode<ContentType> nodeCopy = node.getCopy();
            graphCopy.addNode(nodeCopy);
        }
        // create and add edges for the nodes (preserving edge identifier)
       Collection<GenericEdge<ContentType>> edges = getEdges();
        for (GenericEdge<ContentType> edge : edges) {
            GenericNode<ContentType> source = graphCopy.getNode(edge.getSource().getIdentifier());
            GenericNode<ContentType> target = graphCopy.getNode(edge.getTarget().getIdentifier());
            graphCopy.addEdgeBetween(edge.getIdentifier(), source, target);
        }
        return graphCopy;
    }
}
