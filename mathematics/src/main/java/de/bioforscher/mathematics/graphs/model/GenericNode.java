package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

/**
 * The generic node class is a container for the content, that is specified by the content type.
 *
 * @param <ContentType> The content of the node.
 * @author cl
 */
public class GenericNode<ContentType> extends AbstractNode<GenericNode<ContentType>, Vector> {

    /**
     * The content.
     */
    private ContentType content;

    /**
     * Creates a new generic node with the given identifier.
     *
     * @param identifier The identifier.
     */
    public GenericNode(int identifier) {
        super(identifier);
    }

    /**
     * Creates a new generic node with the given identifier and the specified content.
     *
     * @param identifier The identifier.
     * @param content    The content.
     */
    public GenericNode(int identifier, ContentType content) {
        super(identifier);
        this.content = content;
    }

    /**
     * Returns the content of the node.
     *
     * @return The content of the node.
     */
    public ContentType getContent() {
        return this.content;
    }

    /**
     * Sets the content of the node.
     *
     * @param content The content of the node.
     */
    public void setContent(ContentType content) {
        this.content = content;
    }
}
