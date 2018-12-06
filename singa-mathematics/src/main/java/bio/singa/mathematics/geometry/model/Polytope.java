package bio.singa.mathematics.geometry.model;

import bio.singa.mathematics.vectors.Vector;

import java.util.List;

/**
 * In elementary geometry, a polytope is a geometric object with flat sides, and may exist in any general number of
 * dimensions n as an n-dimensional polytope or n-polytope. For example a two-dimensional polygon is a 2-polytope and a
 * three-dimensional polyhedron is a 3-polytope.
 *
 * @author cl
 */
public interface Polytope<VectorType extends Vector>{

    /**
     * Returns the vertices of the polytope.
     *
     * @return The vertices of the polytype.
     */
    List<VectorType> getVertices();

    /**
     * Returns the vertex with the given vertex identifier.
     *
     * @param vertexIdentifier The vertex identifier.
     * @return The vertex with the given vertex identifier.
     */
    VectorType getVertex(int vertexIdentifier);

    /**
     * Returns the number of vertices.
     *
     * @return The number of vertices.
     */
    default int getNumberOfVertices() {
        return getVertices().size();
    }

}
