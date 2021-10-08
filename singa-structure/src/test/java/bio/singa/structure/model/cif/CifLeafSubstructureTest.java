package bio.singa.structure.model.cif;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class CifLeafSubstructureTest {

    private static Structure structure1C0A;
    private static LeafSubstructure leaf162;
    private static LeafSubstructure leaf21;
    private static LeafSubstructure leafToModify;

    @BeforeAll
    static void prepareData() {
        structure1C0A = StructureParser.cif()
                .pdbIdentifier("1C0A")
                .everything().parse();
        leaf162 = structure1C0A.getLeafSubstructure(new CifLeafIdentifier("1C0A", 1,"B", 162)).get();
        leaf21 = structure1C0A.getLeafSubstructure(new CifLeafIdentifier("1C0A",  1,"A", 21)).get();
        leafToModify = structure1C0A.getLeafSubstructure(new CifLeafIdentifier("1C0A",  1,"B", 163)).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(new CifLeafIdentifier("1C0A", 2, 1, "B", 162), leaf162.getIdentifier());
        assertEquals(new CifLeafIdentifier("1C0A", 1,1, "A", 21), leaf21.getIdentifier());
    }

    @Test
    void getThreeLetterCode() {
        assertEquals("THR", leaf162.getThreeLetterCode());
        assertEquals("H2U", leaf21.getThreeLetterCode());
    }

    @Test
    void getAllAtoms() {
        Collection<? extends Atom> allAtoms = leaf162.getAllAtoms();
        assertEquals(7, allAtoms.size());
    }

    @Test
    void getAtom() {
        int atomIdentifier = 437;
        Optional<? extends Atom> optionalAtom = leaf21.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier(), atomIdentifier);
        assertEquals("OP2", atom.getAtomName());
        assertTrue(new Vector3D(63.941, -2.024, 30.308).almostEqual(atom.getPosition(), 0.001));
    }

    @Test
    void removeAtom() {
        // ATOM   3208 HH22 ARG B  83      37.797  27.994 -88.269  1.00  0.00           H
        int atomIdentifier = 2974;
        Optional<? extends Atom> optionalAtom = leafToModify.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier(), atomIdentifier);
        leafToModify.removeAtom(atomIdentifier);
        // check if it is present in the leaf
        optionalAtom = leafToModify.getAtom(atomIdentifier);
        assertFalse(optionalAtom.isPresent());
        // check if it is present in the structure
        optionalAtom = structure1C0A.getFirstModel().getAtom(atomIdentifier);
        assertFalse(optionalAtom.isPresent());
    }

    @Test
    void getAtomByName() {
        // HETATM  444  C1' H2U B 620A     64.290   3.199  32.742  1.00 78.93           C
        final Optional<? extends Atom> optionalAtom = leaf21.getAtomByName("C1'");
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier(), 444);
        assertEquals("C1'", atom.getAtomName());
        assertTrue(new Vector3D(64.29, 3.199, 32.742).almostEqual(atom.getPosition(), 0.001));
    }

}