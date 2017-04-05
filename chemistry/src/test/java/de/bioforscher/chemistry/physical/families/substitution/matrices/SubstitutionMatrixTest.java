package de.bioforscher.chemistry.physical.families.substitution.matrices;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author fk
 */
public class SubstitutionMatrixTest {

    @Test
    public void shouldLoadSubstitutionMatrics(){
        System.out.println(SubstitutionMatrix.BLOSUM_45.getMatrix().getStringRepresentation());
    }
}