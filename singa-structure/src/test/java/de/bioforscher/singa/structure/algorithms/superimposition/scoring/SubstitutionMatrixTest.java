package de.bioforscher.singa.structure.algorithms.superimposition.scoring;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author cl
 */
public class SubstitutionMatrixTest {

    @Test
    public void getMatrix() throws Exception {
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }

}