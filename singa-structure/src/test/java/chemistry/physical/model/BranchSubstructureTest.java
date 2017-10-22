package chemistry.physical.model;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.oak.OakAminoAcid;
import de.bioforscher.singa.structure.model.oak.OakAtom;
import de.bioforscher.singa.structure.model.oak.OakChain;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider.CARBON;
import static de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider.NITROGEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class BranchSubstructureTest {

    @Test
    public void shouldCreateCopy() {

        OakAtom a1 = new OakAtom(0, NITROGEN, "N", new Vector3D(0.0,1.0,2.0));
        OakAtom a2 = new OakAtom(1, CARBON, "CA", new Vector3D(0.0,1.0,2.0));
        OakAtom a3 = new OakAtom(2, CARBON, "C", new Vector3D(0.0,1.0,2.0));

        OakAminoAcid r1 = new OakAminoAcid(new LeafIdentifier(0), AminoAcidFamily.ALANINE);
        r1.addAtom(a1);
        r1.addAtom(a2);
        r1.addAtom(a3);
        r1.addBondBetween(a1,a2);
        r1.addBondBetween(a2,a3);

        OakChain chain = new OakChain("A");
        chain.addLeafSubstructure(r1);

        OakChain chainCopy = chain.getCopy();
        // by contract
        // class is the same
        assertTrue(chain.getClass() == chainCopy.getClass());
        // the chains are NOT identical
        assertFalse(chain == chainCopy);
        // but equal (all attributes that are crucial to the identification of the chainIdentifier are equal)
        assertTrue(chain.equals(chainCopy));

    }

}