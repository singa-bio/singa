package bio.singa.structure.algorithms.superimposition.scores;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author cl
 */
public class SubstitutionMatrixTest {

    @Test
    public void getMatrix() {
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }

}