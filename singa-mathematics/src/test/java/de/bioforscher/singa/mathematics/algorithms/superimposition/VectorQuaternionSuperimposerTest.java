package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 */
public class VectorQuaternionSuperimposerTest {

    private List<Vector3D> reference;
    private List<Vector3D> candidate;

    @Before
    public void setUp() {
        reference = new ArrayList<>();
        reference.add(new Vector3D(-0.9683594722996112, 8.585195247750672, 31.921580121882982));
        reference.add(new Vector3D(11.480460599181104, 1.1518653477227012, 15.466414202778763));
        reference.add(new Vector3D(-8.979636059776926, -5.9228509076046905, -14.622335100120159));
        reference.add(new Vector3D(-30.1651676477022, 39.91676370891183, -10.761896893612315));
        reference.add(new Vector3D(30.35770258059763, -31.465973396780544, 4.616237669070726));

        candidate = new ArrayList<>();
        candidate.add(new Vector3D(7.557214201253842, 19.818131954467926, 16.478168828224284));
        candidate.add(new Vector3D(1.3360581699972016, -0.5060770735319728, 15.240344737289533));
        candidate.add(new Vector3D(-11.73619504954975, -8.122942855021254, -18.93366303610749));
        candidate.add(new Vector3D(29.91535584593334, 13.613572072519187, -39.013691417117386));
        candidate.add(new Vector3D(-27.072433167634635, -24.80268409843388, 26.22884088771103));
    }

    @Test
    public void calculateSuperimposition() {

//        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(reference, candidate);
//        System.out.println(vectorSuperimposition.getTranslation());

        VectorSuperimposition vectorSuperimposition = VectorQuaternionSuperimposer.calculateVectorSuperimposition(reference, candidate);
    }
}