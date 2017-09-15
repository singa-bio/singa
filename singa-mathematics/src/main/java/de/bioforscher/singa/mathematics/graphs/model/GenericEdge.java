package de.bioforscher.singa.mathematics.graphs.model;

/**
 * The generic edge class connects {@link GenericNode}s with the same specified content type.
 *
 * @param <ContentType> The specified content.
 * @author cl
 */
public class GenericEdge<ContentType> extends AbstractEdge<GenericNode<ContentType>> {

    /**
     * Creates a new generic edge with the given identifier.
     *
     * @param identifier The identifier.
     */
    public GenericEdge(int identifier) {
        super(identifier);
    }

}
