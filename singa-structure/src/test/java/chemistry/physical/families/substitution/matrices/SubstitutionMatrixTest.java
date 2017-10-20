package chemistry.physical.families.substitution.matrices;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author fk
 */
public class SubstitutionMatrixTest {

    @Test
    public void shouldLoadSubstitutionMatrics(){
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }
}