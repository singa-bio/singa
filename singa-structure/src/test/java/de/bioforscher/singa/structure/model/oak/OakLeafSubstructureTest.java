package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class OakLeafSubstructureTest {

    private static LeafSubstructure<?> leaf162;
    private static LeafSubstructure<?> leaf620A;
    private static OakAminoAcid leafToModify;

    @BeforeClass
    public static void prepareData() {
        Structure structure1C0A = StructureParser.pdb().pdbIdentifier("1C0A").parse();
        leaf162 = structure1C0A.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 162)).get();
        leaf620A = structure1C0A.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "B", 620, 'A')).get();
        leafToModify = (OakAminoAcid) structure1C0A.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 161)).get();
    }

    @Test
    public void getIdentifier() {
        assertEquals(new LeafIdentifier("1C0A", 1, "A", 162), leaf162.getIdentifier());
        assertEquals(new LeafIdentifier("1C0A", 1, "B", 620, 'A'), leaf620A.getIdentifier());
    }

    @Test
    public void getThreeLetterCode() {
        assertEquals("Thr", leaf162.getThreeLetterCode());
        assertEquals("H2U", leaf620A.getThreeLetterCode());
    }

    @Test
    public void getAllAtoms() {
        final List<Atom> allAtoms = leaf162.getAllAtoms();
        assertEquals(7, allAtoms.size());
    }

    @Test
    public void getAtom() {
        // HETATM  437  OP2 H2U B 620A     63.941  -2.024  30.308  1.00 82.89           O
        final Optional<Atom> optionalAtom = leaf620A.getAtom(437);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        assertEquals("OP2", optionalAtom.get().getAtomName());
        assertEquals(new Vector3D(63.941, -2.024, 30.308), optionalAtom.get().getPosition());
    }

    @Test
    public void getAtomByName() {
        final Optional<Atom> optionalAtom = leaf620A.getAtomByName("OP2");
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals(new Vector3D(63.941, -2.024, 30.308), optionalAtom.get().getPosition());
    }

    @Test
    public void isAnnotatedAsHeteroAtom() {
        assertTrue(leaf620A.isAnnotatedAsHeteroAtom());
    }

    @Test
    public void getFamily() {
        final StructuralFamily family = leaf620A.getFamily();
        assertEquals("U", family.getThreeLetterCode());
    }

    @Test
    public void addAtom() {
        final int expected = leafToModify.getAllAtoms().size() + 1;
        leafToModify.addAtom(new OakAtom(9999, ElementProvider.SULFUR, "S", new Vector3D()));
        final int actual = leafToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    public void removeAtom() {
        // ATOM   2966  OE2 GLU A 161      45.631  55.119  -0.991  1.00 14.27           O
        final int expected = leafToModify.getAllAtoms().size() - 1;
        leafToModify.removeAtom(2966);
        final int actual = leafToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

}