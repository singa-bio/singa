package de.bioforscher.singa.mathematics.vectors;

import java.util.Collection;

/**
 * Created by Christoph on 14/06/2017.
 */
public class Vectors3D {

    /**
     * Computes the centroid of all vectors in the collection by summing them and dividing by the number of vectors in
     * the collection. This is faster than using the general implementation from the {@link Vectors} class.
     *
     * @param vectors The vectors to calculate the centroid from.
     * @return The centroid.
     */
    public static Vector3D getCentroid(Collection<Vector3D> vectors) {
        int vectorCount = vectors.size();
        double[] sum = new double[3];
        for (Vector3D vector : vectors) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
            sum[2] += vector.getZ();
        }
        return new Vector3D(sum[0] / vectorCount, sum[1] / vectorCount, sum[2] / vectorCount);
    }
}
