package bio.singa.structure.algorithms.superimposition.scores;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author cl
 */
class SubstitutionMatrixTest {

    @Test
    void getMatrix() {
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }

    @Test
    void getAllMatrices() {
        Set<String> stringComparison = new HashSet<>();
        for (SubstitutionMatrix substitutionMatrix : SubstitutionMatrix.values()) {
            stringComparison.add(substitutionMatrix.getMatrix().getStringRepresentation());
        }
        assertEquals(SubstitutionMatrix.values().length, stringComparison.size());
    }
}