package bio.singa.structure.algorithms.superimposition.scores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cl
 */
class SubstitutionMatrixTest {

    @Test
    void getMatrix() {
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }

}