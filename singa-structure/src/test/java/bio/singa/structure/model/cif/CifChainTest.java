package bio.singa.structure.model.cif;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class CifChainTest {

    private static Chain firstChain;
    private static Chain chainToModify;
    private static Structure structure2n5e;

    @BeforeAll
    static void initialize() {
        structure2n5e = StructureParser.cif()
                .pdbIdentifier("2n5e")
                .parse();
        firstChain = structure2n5e.getFirstChain();
        chainToModify = structure2n5e.getFirstModel().getChain("B").get();
    }

    @Test
    void getIdentifier() {
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        Collection<? extends LeafSubstructure> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<? extends LeafSubstructure> optionalLeafSubstructure = firstChain.getLeafSubstructure(new CifLeafIdentifier("2n5e", 1, "A", 10));
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure leafSubstructure = optionalLeafSubstructure.get();
        assertEquals(1, leafSubstructure.getIdentifier().getModelIdentifier());
        assertEquals("A", leafSubstructure.getIdentifier().getChainIdentifier());
        assertEquals("LEU", leafSubstructure.getThreeLetterCode());
        assertEquals(10, leafSubstructure.getIdentifier().getSerial());
    }

    @Test
    void removeLeafSubstructre() {
        // ATOM   4357  N   LEU B 174      -7.551  -8.393 -46.127  1.00  0.00           N
        // ...
        // ATOM   4375 HD23 LEU B 174      -3.489  -6.082 -46.198  1.00  0.00           H
        final CifLeafIdentifier leafIdentifier = new CifLeafIdentifier("2n5e", 1, "B", 98);
        Optional<? extends LeafSubstructure> optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure leafSubstructure = optionalLeafSubstructure.get();
        assertEquals("LEU", leafSubstructure.getThreeLetterCode());
        assertEquals(98, leafSubstructure.getIdentifier().getSerial());
        chainToModify.removeLeafSubstructure(leafIdentifier);
        // check if it is present in the chain
        optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        assertFalse(optionalLeafSubstructure.isPresent());
        // check if it is present in the structure
        optionalLeafSubstructure = structure2n5e.getLeafSubstructure(leafIdentifier);
        assertFalse(optionalLeafSubstructure.isPresent());
    }

    @Test
    void getAtom() {
        // ATOM     32  CD1 PHE A  57       8.392   2.046  -5.789  1.00  0.00           C
        final Optional<? extends Atom> optionalAtom = firstChain.getAtom(32);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomName(), "CD1");
        assertEquals(atom.getAtomIdentifier(), 32);
        assertTrue(new Vector3D(8.392, 2.046, -5.789).almostEqual(atom.getPosition(), 0.0001));
    }

    @Test
    void removeAtom() {
        // ATOM   3208 HH22 ARG B  83      37.797  27.994 -88.269  1.00  0.00           H
        final int atomIdentifier = 3208;
        Optional<? extends Atom> optionalAtom = chainToModify.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier(), atomIdentifier);
        chainToModify.removeAtom(atomIdentifier);
        // check if it is present in the chain
        optionalAtom = chainToModify.getAtom(atomIdentifier);
        assertFalse(optionalAtom.isPresent());
        // check if it is present in the structure
        // keep ini mind: get atom returns the first atom with id 3208 that is found (the one not removed in model 2)
        optionalAtom = structure2n5e.getFirstModel().getAtom(atomIdentifier);
        assertFalse(optionalAtom.isPresent());
    }

    @Test
    void getCopy() {
        final Chain chainCopy = chainToModify.getCopy();
        assertEquals(chainCopy, chainToModify);
        assertNotSame(chainCopy, chainToModify);
        final CifLeafIdentifier leafIdentifier = new CifLeafIdentifier("2n5e", 1, "B", 128);
        // remove a leaf from the copy
        boolean wasRemoved = chainCopy.removeLeafSubstructure(leafIdentifier);
        assertTrue(wasRemoved);
        // copy again
        final Chain chainCopyCopy = chainCopy.getCopy();
        assertEquals(chainCopyCopy, chainCopy);
        assertNotSame(chainCopyCopy, chainCopy);
        // assert that the copy's copy does not contain the leaf
        Optional<? extends LeafSubstructure> optionalLeafSubstructure = chainCopyCopy.getLeafSubstructure(leafIdentifier);
        assertFalse(optionalLeafSubstructure.isPresent());
        // but the original does
        optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        assertTrue(optionalLeafSubstructure.isPresent());
    }

}