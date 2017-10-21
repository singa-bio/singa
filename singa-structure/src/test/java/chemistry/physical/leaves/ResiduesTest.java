package chemistry.physical.leaves;


import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.oak.AminoAcids;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

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