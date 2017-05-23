package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class BranchSubstructureTest {


    @Test
    public void shouldCreateCopyOfSubStructure() {
        // TODO 16/11/16
        // crate substructure with other substructures and atoms
        // first implement other copy methods

        Atom a1 = new RegularAtom(0, NITROGEN, "N", new Vector3D(0.0,1.0,2.0));
        Atom a2 = new RegularAtom(1, CARBON, "CA", new Vector3D(0.0,1.0,2.0));
        Atom a3 = new RegularAtom(2, CARBON, "C", new Vector3D(0.0,1.0,2.0));
        Atom a4 = new RegularAtom(3, HYDROGEN, "H1", new Vector3D(0.0,1.0,2.0));
        Atom a5 = new RegularAtom(4, HYDROGEN, "H2", new Vector3D(0.0,1.0,2.0));
        Atom a6 = new RegularAtom(5, HYDROGEN, "H3", new Vector3D(0.0,1.0,2.0));

        AminoAcid r1 = new AminoAcid(0, AminoAcidFamily.ALANINE);
        r1.addNode(a1);
        r1.addNode(a2);
        r1.addNode(a3);
        r1.addEdgeBetween(a1,a2);
        r1.addEdgeBetween(a2,a3);

        BranchSubstructure chain = new Chain(0);
        chain.addSubstructure(r1);
        chain.addNode(a4);
        chain.addNode(a5);
        chain.addNode(a6);

        Chain chainCopy = (Chain)chain.getCopy();
        // by contract
        // class is the same
        assertTrue(chain.getClass() == chainCopy.getClass());
        // the chains are NOT identical
        assertFalse(chain == chainCopy);
        // but equal (all attributes that are crucial to the identification of the chain are equal)
        assertTrue(chain.equals(chainCopy));

    }

}