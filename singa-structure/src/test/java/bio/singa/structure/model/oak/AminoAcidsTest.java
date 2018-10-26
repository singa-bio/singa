package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author cl
 */
class AminoAcidsTest {

    @Test
    void createVirtualCBAtom() {
        AminoAcid glycine = AminoAcidFamily.GLYCINE.getPrototype();
        assertArrayEquals(new double[]{0.5631065040724832, 1.8146146343509413, -0.6210296794396173},
                AminoAcids.createVirtualCBAtom(glycine).getPosition().getElements(),
                1E-6);
    }

}