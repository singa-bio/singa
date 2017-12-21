package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.elements.ElementProvider;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class OakAtomTest {

    private static OakAtom atom412;
    private static OakAtom atom5444;
    private static OakAtom atomToModify;

    @BeforeClass
    public static void prepareData() throws IOException {
        Structure structure1C0A = StructureParser.online().pdbIdentifier("1C0A").parse();
        // no offset to regular pdb file
        atom412 = (OakAtom) structure1C0A.getAtom(412).get();
        // one offset to regular pdb file
        atom5444 = (OakAtom) structure1C0A.getAtom(5444).get();
        atomToModify = (OakAtom) structure1C0A.getAtom(1000).get();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals(412, (int) atom412.getIdentifier());
        assertEquals(5444, (int) atom5444.getIdentifier());
    }

    @Test
    public void getAtomName() throws Exception {
        assertEquals("N2", atom412.getAtomName());
        assertEquals("N", atom5444.getAtomName());
    }

    @Test
    public void getPosition() throws Exception {
        assertEquals(new Vector3D(62.204, -8.506, 22.574), atom412.getPosition());
        assertEquals(new Vector3D(69.813, 36.839, -11.546), atom5444.getPosition());
    }

    @Test
    public void getElement() throws Exception {
        assertEquals(ElementProvider.NITROGEN, atom412.getElement());
        assertEquals(ElementProvider.NITROGEN, atom5444.getElement());
    }

    @Test
    public void setPosition() throws Exception {
        final Vector3D newPosition = new Vector3D(1.0, 2.0, 3.0);
        atomToModify.setPosition(newPosition);
        assertEquals(newPosition, atomToModify.getPosition());
    }

    @Test
    public void getNeighbours() throws Exception {
        final Set<OakAtom> neighbours = atom412.getNeighbours();
        System.out.println(neighbours);
    }

    @Test
    public void shouldCreateCopyOfAtom() {
        OakAtom atom = new OakAtom(1, ElementProvider.CARBON, "CA", new Vector3D(1.0, 2.0, 3.0));
        Atom copiedAtom = atom.getCopy();

        // by contract
        // class is the same
        assertTrue(atom.getClass() == copiedAtom.getClass());
        // the atoms are NOT identical
        assertFalse(atom == copiedAtom);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertTrue(atom.equals(copiedAtom));
    }

    @Test
    public void shouldCreateCopyOfBond() {
        OakAtom source = new OakAtom(1, ElementProvider.HYDROGEN, "H", new Vector3D(1.0, 2.0, 3.0));
        OakAtom target = new OakAtom(2, ElementProvider.NITROGEN, "N", new Vector3D(4.0, 5.0, 6.0));

        OakBond bond = new OakBond(0);
        bond.setSource(source);
        bond.setTarget(target);
        OakBond copiedBond = new OakBond(bond);

        // by contract
        // class is the same
        assertTrue(bond.getClass() == copiedBond.getClass());
        // the atoms are NOT identical
        assertFalse(bond == copiedBond);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertTrue(bond.equals(copiedBond));
    }


}