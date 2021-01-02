package bio.singa.mathematics.geometry.faces;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static boolean containsVector(Polygon polygon, Vector2D vector) {
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

    /**
     * Returns the minimal bounding box that contains all vertices.
     *
     * @param polygon The contained polygon.
     * @return The containing bounding box.
     */
    public static Rectangle getMinimalBoundingBox(Polygon polygon) {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        // look for minimal and maximal x and y
        for (Vector2D vertex : polygon.getVertices()) {
            double currentX = vertex.getX();
            double currentY = vertex.getY();
            if (currentX < xMin) {
                xMin = currentX;
            }
            if (currentY < yMin) {
                yMin = currentY;
            }
            if (currentX > xMax) {
                xMax = currentX;
            }
            if (currentY > yMax) {
                yMax = currentY;
            }
        }
        return new Rectangle(new Vector2D(xMin, yMin), new Vector2D(xMax, yMax));
    }


    /**
     * Returns a map of all touching line segments. The key is the pair of line segments that touch and the value is the
     * actual line segment that both lines share. The first element of the key pair is the line segment from the first
     * polygon and vice versa.
     *
     * @param first The first polygon.
     * @param second The second polygon.
     *
     * @return A map of touching line segments.
     */
    public static Map<Pair<LineSegment>, LineSegment> getTouchingLineSegments(Polygon first, Polygon second) {

        // groups line segments that are pairwise parallel
        List<List<Pair<LineSegment>>> allParallelSegments = new ArrayList<>();
        List<Pair<LineSegment>> touchingLineSegments = new ArrayList<>();
        Map<Pair<LineSegment>, LineSegment> resultMap = new HashMap<>();

        // determine which lines are parallel
        for (LineSegment firstLineSegment : first.getEdges()) {
            for (LineSegment secondLineSegment : second.getEdges()) {
                // trivial case: if both lines are identical definitely touch
                if (firstLineSegment.isCongruentTo(secondLineSegment)) {
                    resultMap.put(new Pair<>(firstLineSegment, secondLineSegment), firstLineSegment);
                    break;
                }
                // two line segments are parallel if their unit vectors are equal or opposite
                Vector2D firstUnitVector = firstLineSegment.getUnitVector();
                Vector2D secondUnitVector = secondLineSegment.getUnitVector();
                if (unitVectorsAreParallel(firstUnitVector, secondUnitVector)) {
                    // sort them to a list where all line segments are parallel
                    boolean sorted = false;
                    for (List<Pair<LineSegment>> parallelSegmentGroup : allParallelSegments) {
                        Vector2D representativeUnitVector = parallelSegmentGroup.iterator().next().getFirst().getUnitVector();
                        if (unitVectorsAreParallel(firstUnitVector, representativeUnitVector)) {
                            parallelSegmentGroup.add(new Pair<>(firstLineSegment, secondLineSegment));
                            sorted = true;
                            break;
                        }
                    }
                    // create new group if it could not be sorted into existing ones
                    if (!sorted) {
                        List<Pair<LineSegment>> parallelSegmentGroup = new ArrayList<>();
                        parallelSegmentGroup.add(new Pair<>(firstLineSegment, secondLineSegment));
                        allParallelSegments.add(parallelSegmentGroup);
                    }
                }
            }
        }

        // determine which sides touch
        for (List<Pair<LineSegment>> parallelSegmentGroup : allParallelSegments) {
            for (int firstIterator = 0; firstIterator < parallelSegmentGroup.size(); firstIterator++) {
                // creates unit vectors from first segment
                LineSegment firstSegment = parallelSegmentGroup.get(firstIterator).getFirst();
                Vector2D referenceUnitVector = firstSegment.getUnitVector();
                for (int secondIterator = firstIterator; secondIterator < parallelSegmentGroup.size(); secondIterator++) {
                    // when connecting another point to the start of they first segment they run parallel if their unit vectors are equal or opposite
                    LineSegment secondSegment = parallelSegmentGroup.get(secondIterator).getSecond();
                    Vector2D startingPoint = firstSegment.getStartingPoint();
                    Vector2D endingPoint = secondSegment.getEndingPoint();
                    if (startingPoint.equals(endingPoint)) {
                        endingPoint = secondSegment.getStartingPoint();
                    }
                    Vector2D candidateUnitVector = new SimpleLineSegment(startingPoint, endingPoint).getUnitVector();
                    if (unitVectorsAreParallel(referenceUnitVector, candidateUnitVector)) {
                        Pair<LineSegment> segmentPair = new Pair<>(firstSegment, secondSegment);
                        touchingLineSegments.add(segmentPair);
                    }
                }
            }
        }

        // determine resulting touching line segments
        for (Pair<LineSegment> touchingSegmentPair : touchingLineSegments) {
            LineSegment firstSegment = touchingSegmentPair.getFirst();
            LineSegment secondSegment = touchingSegmentPair.getSecond();
            // possible containment relations
            boolean secondStartOnFirst = firstSegment.isOnLine(secondSegment.getStartingPoint());
            boolean secondEndOnFirst = firstSegment.isOnLine(secondSegment.getEndingPoint());
            boolean firstStartOnSecond = secondSegment.isOnLine(firstSegment.getStartingPoint());
            boolean firstEndOnSecond = secondSegment.isOnLine(firstSegment.getEndingPoint());
            // determine what is the case
            if (secondStartOnFirst && secondEndOnFirst) {
                // case 1: second is contained in first
                resultMap.put(touchingSegmentPair, secondSegment);
            } else if (firstStartOnSecond && firstEndOnSecond) {
                // case 2: first is contained in second
                resultMap.put(touchingSegmentPair, firstSegment);
            } else {
                // case 3: neither is contained in the other they only overlap
                if (secondStartOnFirst && firstStartOnSecond) {
                    if (!secondSegment.getStartingPoint().equals(firstSegment.getStartingPoint())) {
                        resultMap.put(touchingSegmentPair, new SimpleLineSegment(secondSegment.getStartingPoint(), firstSegment.getStartingPoint()));
                    }
                } else if (secondStartOnFirst && firstEndOnSecond) {
                    if (!secondSegment.getStartingPoint().equals(firstSegment.getEndingPoint())) {
                        resultMap.put(touchingSegmentPair, new SimpleLineSegment(secondSegment.getStartingPoint(), firstSegment.getEndingPoint()));
                    }
                } else if (secondEndOnFirst && firstStartOnSecond) {
                    if (!secondSegment.getEndingPoint().equals(firstSegment.getStartingPoint())) {
                        resultMap.put(touchingSegmentPair, new SimpleLineSegment(secondSegment.getEndingPoint(), firstSegment.getStartingPoint()));
                    }
                } else if (secondEndOnFirst && firstEndOnSecond) {
                    if (!secondSegment.getEndingPoint().equals(firstSegment.getEndingPoint())) {
                        resultMap.put(touchingSegmentPair, new SimpleLineSegment(secondSegment.getEndingPoint(), firstSegment.getEndingPoint()));
                    }
                }
            }

        }

        return resultMap;
    }

    private static boolean unitVectorsAreParallel(Vector2D firstUnitVector, Vector2D secondUnitVector) {
        return (Math.abs(firstUnitVector.getX()) == Math.abs(secondUnitVector.getX())) &&
                (Math.abs(firstUnitVector.getY()) == Math.abs(secondUnitVector.getY()));
    }

}
