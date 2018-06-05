package de.bioforscher.singa.mathematics.quaternions;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 */
public class QuaternionsTest {

    @Test
    public void shouldCalculateRelativeOrientation() {
        List<Vector3D> reference = new ArrayList<>();
        reference.add(new Vector3D(-1.31335947229961, 6.132195247750678, 26.597580121882984));
        reference.add(new Vector3D(11.135460599181105, -1.3011346522772929, 10.142414202778763));
        reference.add(new Vector3D(-9.324636059776925, -8.375850907604685, -19.94633510012016));
        reference.add(new Vector3D(-30.5101676477022, 37.463763708911834, -16.085896893612315));
        reference.add(new Vector3D(30.01270258059763, -33.91897339678054, -0.707762330929274));

        List<Vector3D> candidate = new ArrayList<>();
        candidate.add(new Vector3D(7.557214201253842, 19.818131954467926, 16.47816882822429));
        candidate.add(new Vector3D(1.3360581699972016, -0.5060770735319743, 15.240344737289538));
        candidate.add(new Vector3D(-11.73619504954975, -8.122942855021256, -18.933663036107482));
        candidate.add(new Vector3D(29.91535584593334, 13.613572072519185, -39.01369141711738));
        candidate.add(new Vector3D(-27.072433167634635, -24.80268409843388, 26.228840887711037));

        Quaternion quaternion = Quaternions.relativeOrientation(reference, candidate);
        Assert.assertArrayEquals(new double[]{-0.314536338481798, -0.21739989933815962,
                -0.5963273276405343, -0.7058313494393125}, quaternion.getElements(), 1E-6);
    }
}