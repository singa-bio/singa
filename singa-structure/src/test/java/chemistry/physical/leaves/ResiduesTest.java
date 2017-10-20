package chemistry.physical.leaves;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import org.junit.Test;

/**
 * @author fk
 */
public class ResiduesTest {
    @Test
    public void shouldCreateVirtualCBAtom() throws Exception {
        AminoAcid glycine = AminoAcidFamily.GLYCINE.getPrototype();
        assertArrayEquals(new double[]{0.5631065040724832, 1.8146146343509413, -0.6210296794396173},
                AminoAcids.createVirtualCBAtom(glycine).getPosition().getElements(),
                1E-6);
    }
}