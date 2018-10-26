package bio.singa.mathematics.algorithms.geometry;

import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * The Sutherland–Hodgman algorithm is used for clipping polygons.
 * Requires counter-clockwise sorting of the polygon vertices.
 *
 * @author cl
 */
public class SutherandHodgmanClipping {

    private List<double[]> clipper;
    private List<double[]> result;

    private Polygon resultPolygon;

    public static Polygon clip(Polygon subjectPolygon, Polygon clipPolygon) {
        SutherandHodgmanClipping sutherandHodgmanClipping = new SutherandHodgmanClipping(subjectPolygon, clipPolygon);
        return sutherandHodgmanClipping.getResultPolygon();
    }

    private SutherandHodgmanClipping(Polygon subjectPolygon, Polygon clipPolygon) {
        // https://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping#Java
        // TODO clean up
        List<double[]> subject = new ArrayList<>();
        for (Vector2D vector2D : subjectPolygon.getVertices()) {
            subject.add(new double[] {vector2D.getX(), vector2D.getY()});
        }

        result = new ArrayList<>(subject);

        clipper = new ArrayList<>();
        for (Vector2D vector2D : clipPolygon.getVertices()) {
            clipper.add(new double[] {vector2D.getX(), vector2D.getY()});
        }

        clipPolygon();

        List<Vector2D> vectors = new ArrayList<>();
        for (double[] value : result) {
            vectors.add(new Vector2D(value));
        }

        resultPolygon = new VertexPolygon(vectors);

    }

    public Polygon getResultPolygon() {
        return resultPolygon;
    }

    private void clipPolygon() {
        int len = clipper.size();
        for (int i = 0; i < len; i++) {

            int len2 = result.size();
            List<double[]> input = result;
            result = new ArrayList<>(len2);

            double[] A = clipper.get((i + len - 1) % len);
            double[] B = clipper.get(i);

            for (int j = 0; j < len2; j++) {

                double[] P = input.get((j + len2 - 1) % len2);
                double[] Q = input.get(j);

                if (isInside(A, B, Q)) {
                    if (!isInside(A, B, P))
                        result.add(intersection(A, B, P, Q));
                    result.add(Q);
                } else if (isInside(A, B, P))
                    result.add(intersection(A, B, P, Q));
            }
        }
    }

    private boolean isInside(double[] a, double[] b, double[] c) {
        return (a[0] - c[0]) * (b[1] - c[1]) > (a[1] - c[1]) * (b[0] - c[0]);
    }

    private double[] intersection(double[] a, double[] b, double[] p, double[] q) {
        double A1 = b[1] - a[1];
        double B1 = a[0] - b[0];
        double C1 = A1 * a[0] + B1 * a[1];

        double A2 = q[1] - p[1];
        double B2 = p[0] - q[0];
        double C2 = A2 * p[0] + B2 * p[1];

        double det = A1 * B2 - A2 * B1;
        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;

        return new double[]{x, y};
    }

}
