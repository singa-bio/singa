package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.chemistry.physical.proteins.Chain;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.chemistry.physical.proteins.ResidueType;
import de.bioforscher.mathematics.vectors.Vector3D;
import org.junit.Test;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.CARBON;
import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.HYDROGEN;
import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.NITROGEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by fkaiser on 13.11.16.
 */
public class SubStructureTest {


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

        Residue r1 = new Residue(0, ResidueType.ALANINE);
        r1.addNode(a1);
        r1.addNode(a2);
        r1.addNode(a3);
        r1.connect(a1,a2);
        r1.connect(a2,a3);

        SubStructure chain = new Chain(0);
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

    @Test
    public void getAtomContainingSubStructures() throws Exception {
        Structure structure = PDBParserService.parsePDBFile(Thread.currentThread()
                                                            .getContextClassLoader()
                                                            .getResource("1pqs.pdb").getPath());
        SubStructure firstsub = structure.getSubstructures().stream().findFirst().get();
        System.out.println(firstsub);
        System.out.println(firstsub.getAtomContainingSubstructures());
        SubStructure secondSub = firstsub.getSubstructures().stream().findFirst().get();
        System.out.println(secondSub);
        System.out.println(secondSub.getAtomContainingSubstructures());
        SubStructure thirdSub = secondSub.getSubstructures().stream().findFirst().get();
        System.out.println(thirdSub);
        System.out.println(thirdSub.getAtomContainingSubstructures());
    }
}