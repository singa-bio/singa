package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.List;

/**
 * @author cl
 */
public class Polygons {

    public static final int OUTSIDE = -1;
    public static final int ON_LINE = 0;
    public static final int INSIDE = 1;

    //
    //  The function will return YES if the point x,y is inside the polygon, or
    //  NO if it is not.  If the point is exactly on the edge of the polygon,
    //  then the function may return YES or NO.
    //

    /**
     * Returns true, if the vector is inside the polygon, or false if not. If the point is exactly on the edge it may
     * return true or false.
     *
     * @param polygon The polygon.
     * @param vector The vector.
     * @return True, if the vector is inside the polygon, or false if not.
     */
    public static boolean isInside(Polygon polygon, Vector2D vector) {
        // checks complex (convex and intersecting polygons)
        // http://alienryderflex.com/polygon
        int polyCorners = polygon.getNumberOfVertices();
        int j = polyCorners - 1;
        boolean oddNodes = false;

        List<Vector2D> vertices = polygon.getVertices();
        double x = vector.getX();
        double y = vector.getY();
        for (int i = 0; i < polyCorners; i++) {
            double polyXi = vertices.get(i).getX();
            double polyYi = vertices.get(i).getY();
            double polyXj = vertices.get(j).getX();
            double polyYj = vertices.get(j).getY();
            if ((polyYi < y && polyYj >= y || polyYj < y && polyYi >= y) && (polyXi <= x || polyXj <= x)) {
                oddNodes ^= (polyXi + (y - polyYi) / (polyYj - polyYi) * (polyXj - polyXi) < x);
            }
            j = i;
        }
        return oddNodes;
    }

}
