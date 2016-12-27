package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;

/**
 * Created by Christoph Leberecht on 27/12/2016.
 */
public class VectorExamples {

    public static void main(String[] args) {

        Vector basketBall = new Vector2D(20,-5);
        Vector tennisBall = new Vector2D(41,17);

        double distanceEuclidean = basketBall.distanceTo(tennisBall);
        System.out.println(distanceEuclidean);

        double distanceManhattan = basketBall.distanceTo(tennisBall, VectorMetricProvider.MANHATTAN_METRIC);
        System.out.println(distanceManhattan);

        double combinedDistance = basketBall.add(tennisBall).getMagnitude();
        System.out.println(combinedDistance);

        double angle = Math.toDegrees(basketBall.angleTo(tennisBall));
        System.out.println(angle);

    }

}
