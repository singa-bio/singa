package bio.singa.mathematics.geometry.edges;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.LinkedList;

/**
 * @author cl
 */
public class VectorPaths {

    private VectorPaths() {

    }

    /**
     * This operation scales the path by a constant factor and returns a new scaled path.
     *
     * @param path The path to be scaled.
     * @param scalingFactor The scaling factor.
     * @return A new path scaled by the factor.
     */
    public static VectorPath scale(VectorPath path, double scalingFactor) {
        LinkedList<Vector2D> scaledSegments = new LinkedList<>();
        for (Vector2D vertex : path.getSegments()) {
            scaledSegments.add(vertex.multiply(scalingFactor));
        }
        return new VectorPath(scaledSegments);
    }

    /**
     * This operation reduces the number of segments by removing every second vector.
     *
     * @param path The path to be reduced.
     * @return A new, reduced path.
     */
    public static VectorPath reduce(VectorPath path) {
        LinkedList<Vector2D> reducedVectors = new LinkedList<>();
        for (int index = 0; index < path.getSegments().size() - 1; index++) {
            if (index % 2 == 0) {
                reducedVectors.add(path.getSegments().get(index));
            }
        }
        return new VectorPath(reducedVectors);
    }

}
