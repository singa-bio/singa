package de.bioforscher.singa.mathematics.graphs.model;

/**
 * A undirected edge is the simplest instantiable implementation of edges for regular graphs.
 *
 * @author cl
 */
public class UndirectedEdge extends AbstractEdge<RegularNode> {

    /**
     * Creates a new edge with the given identifier.
     *
     * @param identifier The identifier.
     */
    public UndirectedEdge(int identifier) {
        super(identifier);
    }

}
