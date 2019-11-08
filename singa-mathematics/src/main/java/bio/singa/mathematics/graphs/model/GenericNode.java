package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * The generic node class is a container for the content, that is specified by the content type.
 *
 * @param <ContentType> The content of the node.
 * @author cl
 */
public class GenericNode<ContentType> extends AbstractNode<GenericNode<ContentType>, Vector2D, Integer> {

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
     * @param content The content.
     */
    public GenericNode(int identifier, ContentType content) {
        super(identifier, Vectors.generateRandom2DVector(new Rectangle(500, 500)));
        this.content = content;
    }

    private GenericNode(GenericNode<ContentType> node) {
        super(node);
        ContentType content = node.getContent();
        if (Collection.class.isAssignableFrom(content.getClass()) && !content.equals(Collections.emptyList())) {
            if (((Collection) content).size() == 1) {
                // for singleton lists
                this.content = content;
            } else {
                try {
                    this.content = (ContentType) content.getClass().getConstructor(Collection.class).newInstance(content);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                    throw new UnsupportedOperationException("Instance types must match to copy successfully.");
                }
            }
        } else {
            this.content = content;
        }
    }

    /**
     * Returns the content of the node.
     *
     * @return The content of the node.
     */
    public ContentType getContent() {
        return content;
    }

    /**
     * Sets the content of the node.
     *
     * @param content The content of the node.
     */
    public void setContent(ContentType content) {
        this.content = content;
    }

    @Override
    public GenericNode<ContentType> getCopy() {
        return new GenericNode<>(this);
    }

    @Override
    public String toString() {
        return "Node (" + getContent() + ")";
    }
}
