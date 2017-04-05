package de.bioforscher.singa.chemistry.physical.families.substitution.matrices;

import org.junit.Test;

/**
 * @author fk
 */
public class SubstitutionMatrixTest {

    @Test
    public void shouldLoadSubstitutionMatrics(){
        System.out.println(SubstitutionMatrix.BLOSUM_45.getMatrix().getStringRepresentation());
    }
}