package bio.singa.structure.model.oak;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.elements.ElementProvider;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class OakAtomTest {

    private static OakAtom atom412;
    private static OakAtom atom5444;
    private static OakAtom atomToModify;

    @BeforeAll
    static void initialize() {
        Structure structure1C0A = StructureParser.pdb().pdbIdentifier("1C0A").parse();
        // no offset to regular pdb file
        atom412 = (OakAtom) structure1C0A.getAtom(412).get();
        // one offset to regular pdb file
        atom5444 = (OakAtom) structure1C0A.getAtom(5444).get();
        atomToModify = (OakAtom) structure1C0A.getAtom(1000).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(412, (int) atom412.getAtomIdentifier());
        assertEquals(5444, (int) atom5444.getAtomIdentifier());
    }

    @Test
    void getAtomName() {
        assertEquals("N2", atom412.getAtomName());
        assertEquals("N", atom5444.getAtomName());
    }

    @Test
    void getPosition() {
        assertEquals(new Vector3D(62.204, -8.506, 22.574), atom412.getPosition());
        assertEquals(new Vector3D(69.813, 36.839, -11.546), atom5444.getPosition());
    }

    @Test
    void getElement() {
        assertEquals(ElementProvider.NITROGEN, atom412.getElement());
        assertEquals(ElementProvider.NITROGEN, atom5444.getElement());
    }

    @Test
    void setPosition() {
        final Vector3D newPosition = new Vector3D(1.0, 2.0, 3.0);
        atomToModify.setPosition(newPosition);
        assertEquals(newPosition, atomToModify.getPosition());
    }

    @Test
    void getNeighbours() {
        final Set<OakAtom> neighbours = atom412.getNeighbours();
        System.out.println(neighbours);
    }

    @Test
    void shouldCreateCopyOfAtom() {
        OakAtom atom = new OakAtom(1, ElementProvider.CARBON, "CA", new Vector3D(1.0, 2.0, 3.0));
        Atom copiedAtom = atom.getCopy();

        // by contract
        // class is the same
        assertSame(atom.getClass(), copiedAtom.getClass());
        // the atoms are NOT identical
        assertNotSame(atom, copiedAtom);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertEquals(atom, copiedAtom);
    }

    @Test
    void shouldCreateCopyOfBond() {
        OakAtom source = new OakAtom(1, ElementProvider.HYDROGEN, "H", new Vector3D(1.0, 2.0, 3.0));
        OakAtom target = new OakAtom(2, ElementProvider.NITROGEN, "N", new Vector3D(4.0, 5.0, 6.0));

        OakBond bond = new OakBond(0);
        bond.setSource(source);
        bond.setTarget(target);
        OakBond copiedBond = new OakBond(bond);

        // by contract
        // class is the same
        assertSame(bond.getClass(), copiedBond.getClass());
        // the atoms are NOT identical
        assertNotSame(bond, copiedBond);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertEquals(bond, copiedBond);
    }


}