package chemistry.physical.families.substitution.matrices;

import de.bioforscher.singa.structure.algorithms.superimposition.scoring.SubstitutionMatrix;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author fk
 */
public class SubstitutionMatrixTest {

    @Test
    public void shouldLoadSubstitutionMatrix(){
        assertNotNull(SubstitutionMatrix.BLOSUM_45.getMatrix());
    }
}