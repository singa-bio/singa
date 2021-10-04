package bio.singa.structure.model.mmtf;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class MmtfChainTest {

    private static Chain firstChain;
    private static Chain chainToModify;
    private static Structure structure2N5E;

    @BeforeAll
    static void initialize() {
        structure2N5E = StructureParser.mmtf()
                .pdbIdentifier("2n5e")
                .everything().parse();
        firstChain = structure2N5E.getFirstChain();
        chainToModify = structure2N5E.getChain(1, "B").get();
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
        // ATOM    154  N   LEU A  64      13.596   6.125 -14.412  1.00  0.00           N
        // ..
        // ATOM    172 HD23 LEU A  64      11.462   1.727 -16.563  1.00  0.00           H
        Optional<? extends LeafSubstructure> optionalLeafSubstructure = firstChain.getLeafSubstructure(new PdbLeafIdentifier("2N5E", 1, "A", 64));
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure leafSubstructure = optionalLeafSubstructure.get();
        assertEquals(1, leafSubstructure.getIdentifier().getModelIdentifier());
        assertEquals("A", leafSubstructure.getIdentifier().getChainIdentifier());
        assertEquals("LEU", leafSubstructure.getThreeLetterCode());
        assertEquals(64, leafSubstructure.getIdentifier().getSerial());
    }

    @Test
    void removeLeafSubstructre() {
        // ATOM   4357  N   LEU B 174      -7.551  -8.393 -46.127  1.00  0.00           N
        // ...
        // ATOM   4375 HD23 LEU B 174      -3.489  -6.082 -46.198  1.00  0.00           H
        final PdbLeafIdentifier leafIdentifier = new PdbLeafIdentifier("2N5E", 1, "B", 174);
        Optional<? extends LeafSubstructure> optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure leafSubstructure = optionalLeafSubstructure.get();
        assertEquals("LEU", leafSubstructure.getThreeLetterCode());
        assertEquals(174, leafSubstructure.getIdentifier().getSerial());
        chainToModify.removeLeafSubstructure(leafIdentifier);
        // check if it is present in the chain
        optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        assertTrue(!optionalLeafSubstructure.isPresent());
        // check if it is present in the structure
        optionalLeafSubstructure = structure2N5E.getLeafSubstructure(leafIdentifier);
        assertTrue(!optionalLeafSubstructure.isPresent());
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
        assertEquals(atom.getPosition(), new Vector3D(8.392000198364258, 2.0460000038146973, -5.789000034332275));
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
        assertTrue(!optionalAtom.isPresent());
        // check if it is present in the structure
        // keep ini mind: get atom returns the first atom with id 3208 that is found (the one not removed in model 2)
        optionalAtom = structure2N5E.getModel(1).get().getAtom(atomIdentifier);
        assertTrue(!optionalAtom.isPresent());
    }

    @Test
    void getCopy() {
        final Chain chainCopy = chainToModify.getCopy();
        assertEquals(chainCopy, chainToModify);
        assertNotSame(chainCopy, chainToModify);
        // ATOM   4815  N   SER B 204      27.480   2.711 -26.221  1.00  0.00           N
        // ...
        // ATOM   4825  HG  SER B 204      26.503   5.742 -27.310  1.00  0.00           H
        final PdbLeafIdentifier leafIdentifier = new PdbLeafIdentifier("2N5E", 1, "B", 204);
        // remove a leaf from the copy
        chainCopy.removeLeafSubstructure(leafIdentifier);
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