package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.families.ResidueFamily;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author fk
 */
public class ResiduesTest {
    @Test
    public void shouldCreateVirtualCBAtom() throws Exception {
        Residue glycine = ResidueFamily.GLYCINE.getPrototype();
        assertArrayEquals(new double[]{0.5631065040724832, 1.8146146343509413, -0.6210296794396173},
                Residues.createVirtualCBAtom(glycine).getPosition().getElements(),
                1E-6);
    }
}