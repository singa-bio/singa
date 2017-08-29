package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

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

    public GenericNode<ContentType> addNode(ContentType content) {
        final GenericNode<ContentType> genericNode = new GenericNode<>(nextNodeIdentifier(), content);
        addNode(genericNode);
        return genericNode;
    }

    public boolean containsNodeWithContent(ContentType content) {
        for (GenericNode<ContentType> genericNode : getNodes()) {
            if (genericNode.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }

    public Optional<GenericNode<ContentType>> getNodeWithContent(ContentType content) {
        for (GenericNode<ContentType> genericNode : getNodes()) {
            if (genericNode.getContent().equals(content)) {
                return Optional.of(genericNode);
            }
        }
        return Optional.empty();
    }

}
