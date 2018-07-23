package bio.singa.mathematics.algorithms.geometry;

import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

/**
 * Based on Graham Scan
 *
 * @author cl
 */
public class ConvexHull {

    private final List<Vector2D> vectors;
    private final Deque<Vector2D> stack;

    public ConvexHull(Collection<Vector2D> vectors) {
        this.vectors = new ArrayList<>(vectors);
        stack = new ArrayDeque<>();
    }

    public static ConvexHull calculateHullFor(Collection<Vector2D> vectors) {
        if (vectors.size() < 3) {
            ConvexHull convexHull = new ConvexHull(vectors);
            vectors.forEach(convexHull.stack::push);
            return convexHull;
        }

        ConvexHull convexHull = new ConvexHull(vectors);
        // find the element(s) with the minimal y value
        List<Vector2D> vectorsWithMinimalY = Vectors.getVectorsWithMinimalValueForIndex(convexHull.vectors, Vector2D.Y_INDEX);
        // in case that there are multiple minimal elements choose the one that also has the minimal x value
        Vector2D referenceVector = Vectors.getVectorsWithMinimalValueForIndex(vectorsWithMinimalY, Vector2D.X_INDEX).iterator().next();
        // sort the points by angle to the reference vector
        convexHull.vectors.sort(comparing(vector -> {
            double deltaY = referenceVector.getY() - vector.getY();
            double deltaX = referenceVector.getX() - vector.getX();
            return Math.atan2(deltaY, deltaX);
        }, reverseOrder()));
        // initialize actual graham scan algorithm
        convexHull.stack.push(convexHull.vectors.get(0));
        convexHull.stack.push(convexHull.vectors.get(1));
        convexHull.stack.push(convexHull.vectors.get(2));
        int currentIndex = 3;
        int totalIterations = vectors.size();
        // for each remaining vector
        while (currentIndex < totalIterations) {
            // remove all vectors that would result in clockwise turns
            while (getTurnDirection(convexHull.peekTop(), convexHull.peekNextToTop(), convexHull.vectors.get(currentIndex)) == TurnDirection.CLOCKWISE) {
                convexHull.stack.pop();
            }
            // add next vector
            convexHull.stack.push(convexHull.vectors.get(currentIndex));
            currentIndex++;
        }
        // connect first and last
        convexHull.stack.push(convexHull.vectors.get(0));
        return convexHull;
    }

    private static TurnDirection getTurnDirection(Vector2D first, Vector2D second, Vector2D third) {
        // (p2.x - p1.x)*(p3.y - p1.y) - (p2.y - p1.y)*(p3.x - p1.x)
        double direction = (second.getX() - first.getX()) * (third.getY() - first.getY()) - (second.getY() - first.getY()) * (third.getX() - first.getX());
        if (direction > 0) {
            return TurnDirection.COUNTER_CLOCKWISE;
        } else if (direction < 0) {
            return TurnDirection.CLOCKWISE;
        } else {
            return TurnDirection.COLINEAR;
        }
    }

    private Vector2D peekTop() {
        return stack.peek();
    }

    private Vector2D peekNextToTop() {
        final Vector2D top = stack.pop();
        final Vector2D nextToTop = stack.peek();
        stack.push(top);
        return nextToTop;
    }

    public List<Vector2D> getAllVectors() {
        return vectors;
    }

    public List<Vector2D> getHull() {
        return new ArrayList<>(stack);
    }

    public List<Vector2D> getNonHullVectors() {
        return vectors.stream()
                .filter(vector -> !getHull().contains(vector))
                .collect(Collectors.toList());
    }

    private enum TurnDirection {
        COUNTER_CLOCKWISE,
        CLOCKWISE,
        COLINEAR
    }

}
