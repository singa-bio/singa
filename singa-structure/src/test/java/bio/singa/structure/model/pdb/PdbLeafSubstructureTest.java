package bio.singa.structure.model.pdb;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class PdbLeafSubstructureTest {

    private static LeafSubstructure leaf162;
    private static LeafSubstructure leaf620A;
    private static PdbAminoAcid leafToModify;

    @BeforeAll
    static void prepareData() {
        Structure structure1c0a = StructureParser.pdb().pdbIdentifier("1c0a").parse();
        leaf162 = structure1c0a.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "A", 162)).get();
        leaf620A = structure1c0a.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "B", 620, 'A')).get();
        leafToModify = (PdbAminoAcid) structure1c0a.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "A", 161)).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(new PdbLeafIdentifier("1c0a", 1, "A", 162), leaf162.getIdentifier());
        assertEquals(new PdbLeafIdentifier("1c0a", 1, "B", 620, 'A'), leaf620A.getIdentifier());
    }

    @Test
    void getThreeLetterCode() {
        assertEquals("THR", leaf162.getThreeLetterCode());
        assertEquals("H2U", leaf620A.getThreeLetterCode());
    }

    @Test
    void getAllAtoms() {
        Collection<? extends Atom> allAtoms = leaf162.getAllAtoms();
        assertEquals(7, allAtoms.size());
    }

    @Test
    void getAtom() {
        // HETATM  437  OP2 H2U B 620A     63.941  -2.024  30.308  1.00 82.89           O
        final Optional<? extends Atom> optionalAtom = leaf620A.getAtom(437);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        assertEquals("OP2", optionalAtom.get().getAtomName());
        assertEquals(new Vector3D(63.941, -2.024, 30.308), optionalAtom.get().getPosition());
    }

    @Test
    void getAtomByName() {
        final Optional<? extends Atom> optionalAtom = leaf620A.getAtomByName("OP2");
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals(new Vector3D(63.941, -2.024, 30.308), optionalAtom.get().getPosition());
    }

    @Test
    void isAnnotatedAsHeteroAtom() {
        assertTrue(leaf620A.isAnnotatedAsHeteroAtom());
    }

    @Test
    void getFamily() {
        final StructuralFamily family = leaf620A.getFamily();
        assertEquals("U", family.getThreeLetterCode());
    }

    @Test
    void addAtom() {
        final int expected = leafToModify.getAllAtoms().size() + 1;
        leafToModify.addAtom(new PdbAtom(9999, ElementProvider.SULFUR, "S", new Vector3D()));
        final int actual = leafToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    void removeAtom() {
        // ATOM   2966  OE2 GLU A 161      45.631  55.119  -0.991  1.00 14.27           O
        final int expected = leafToModify.getAllAtoms().size() - 1;
        leafToModify.removeAtom(2966);
        final int actual = leafToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

}