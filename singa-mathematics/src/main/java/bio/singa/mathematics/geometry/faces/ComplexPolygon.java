package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class ComplexPolygon implements Polygon {

    private final List<Vector2D> vertices;

    public ComplexPolygon(List<Vector2D> vertices) {
        this.vertices = vertices;
    }

    public ComplexPolygon(Vector2D... vertices) {
        this.vertices = Arrays.asList(vertices);
    }

    @Override
    public Polygon getCopy() {
        return null;
    }

    @Override
    public void move(Vector2D targetLocation) {

    }

    @Override
    public void scale(double scalingFactor) {

    }

    @Override
    public Set<Vector2D> reduce(int times) {
        return null;
    }

    @Override
    public List<Vector2D> getVertices() {
        return vertices;
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return vertices.get(vertexIdentifier);
    }

}
