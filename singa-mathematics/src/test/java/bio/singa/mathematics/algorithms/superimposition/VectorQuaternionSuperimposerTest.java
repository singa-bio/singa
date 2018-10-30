package bio.singa.mathematics.algorithms.superimposition;

import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.mathematics.vectors.Vector3D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static bio.singa.mathematics.NumberConceptAssertion.assertMatrixEquals;
import static bio.singa.mathematics.NumberConceptAssertion.assertVectorEquals;

/**
 * @author fk
 */
class VectorQuaternionSuperimposerTest {

    private static List<Vector3D> reference;
    private static List<Vector3D> candidate;

    @BeforeAll
    static void initialize() {
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
    void calculateSuperimposition() {
        VectorSuperimposition vectorSuperimposition = VectorQuaternionSuperimposer.calculateVectorSuperimposition(reference, candidate);
        assertVectorEquals(new Vector3D(0.34500000000000375, 2.4529999999999923, 5.3240000000000025),
                vectorSuperimposition.getTranslation(), 0.0);
        assertMatrixEquals(new RegularMatrix(new double[][] {{0.19,  0.98, 0.07},{-0.71, 0.09, 0.70}, {0.68, -0.18, 0.71}}),
                vectorSuperimposition.getRotation(), 1e-2);
    }
}