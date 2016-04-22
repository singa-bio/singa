package de.bioforscher.mathematics.geometry.model;

import de.bioforscher.mathematics.vectors.Vector;

/**
 * In elementary geometry, a polytope is a geometric object with flat sides, and
 * may exist in any general number of dimensions n as an n-dimensional polytope
 * or n-polytope. For example a two-dimensional polygon is a 2-polytope and a
 * three-dimensional polyhedron is a 3-polytope.
 *
 * @author Christoph
 */
public interface Polytope<VectorType extends Vector> {

    VectorType[] getVertices();

    VectorType getVertex(int vertexIdentifier);

}
