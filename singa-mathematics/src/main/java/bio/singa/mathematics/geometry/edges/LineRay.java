package bio.singa.mathematics.geometry.edges;

import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class LineRay {

    private final Vector2D origin;
    private final Vector2D direction;

    public LineRay(Vector2D firstPoint, Vector2D secondPoint) {
        origin = firstPoint;
        direction = firstPoint.subtract(secondPoint).normalize();
    }

    public Vector2D getOrigin() {
        return origin;
    }

    public Vector2D getDirection() {
        return direction;
    }
}
