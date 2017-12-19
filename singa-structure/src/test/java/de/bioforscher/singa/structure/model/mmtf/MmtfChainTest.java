package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class MmtfChainTest {

    private static Chain firstChain;
    private static Chain chainToModify;
    private static Structure structure2N5E;

    @BeforeClass
    public static void prepareData() throws IOException {
        structure2N5E = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("2N5E"));
        firstChain = structure2N5E.getFirstChain();
        chainToModify = structure2N5E.getChain(1, "B").get();
    }

    @Test
    public void getIdentifier() {
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() {
        final List<LeafSubstructure<?>> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() {
        // ATOM    154  N   LEU A  64      13.596   6.125 -14.412  1.00  0.00           N
        // ..
        // ATOM    172 HD23 LEU A  64      11.462   1.727 -16.563  1.00  0.00           H
        Optional<LeafSubstructure<?>> optionalLeafSubstructure = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 64));
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure<?> leafSubstructure = optionalLeafSubstructure.get();
        assertEquals(1, leafSubstructure.getIdentifier().getModelIdentifier());
        assertEquals("A", leafSubstructure.getIdentifier().getChainIdentifier());
        assertEquals("Leu", leafSubstructure.getThreeLetterCode());
        assertEquals(64, leafSubstructure.getIdentifier().getSerial());
    }

    @Test
    public void removeLeafSubstructre() {
        // ATOM   4357  N   LEU B 174      -7.551  -8.393 -46.127  1.00  0.00           N
        // ...
        // ATOM   4375 HD23 LEU B 174      -3.489  -6.082 -46.198  1.00  0.00           H
        final LeafIdentifier leafIdentifier = new LeafIdentifier("2N5E", 1, "B", 174);
        Optional<LeafSubstructure<?>> optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        if (!optionalLeafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafSubstructure<?> leafSubstructure = optionalLeafSubstructure.get();
        assertEquals("Leu", leafSubstructure.getThreeLetterCode());
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
    public void getAtom() {
        // ATOM     32  CD1 PHE A  57       8.392   2.046  -5.789  1.00  0.00           C
        final Optional<Atom> optionalAtom = firstChain.getAtom(32);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomName(), "CD1");
        assertEquals(atom.getAtomIdentifier().intValue(), 32);
        assertEquals(atom.getPosition(), new Vector3D(8.392000198364258, 2.0460000038146973, -5.789000034332275));
    }

    @Test
    public void removeAtom() {
        // ATOM   3208 HH22 ARG B  83      37.797  27.994 -88.269  1.00  0.00           H
        final int atomIdentifier = 3208;
        Optional<Atom> optionalAtom = chainToModify.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier().intValue(), atomIdentifier);
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
    public void getCopy() {
        final Chain chainCopy = chainToModify.getCopy();
        assertTrue(chainCopy.equals(chainToModify));
        assertFalse(chainCopy == chainToModify);
        // ATOM   4815  N   SER B 204      27.480   2.711 -26.221  1.00  0.00           N
        // ...
        // ATOM   4825  HG  SER B 204      26.503   5.742 -27.310  1.00  0.00           H
        final LeafIdentifier leafIdentifier = new LeafIdentifier("2N5E", 1, "B", 204);
        // remove a leaf from the copy
        chainCopy.removeLeafSubstructure(leafIdentifier);
        // copy again
        final Chain chainCopyCopy = chainCopy.getCopy();
        assertTrue(chainCopyCopy.equals(chainCopy));
        assertFalse(chainCopyCopy == chainCopy);
        // assert that the copy's copy does not contain the leaf
        Optional<LeafSubstructure<?>> optionalLeafSubstructure = chainCopyCopy.getLeafSubstructure(leafIdentifier);
        assertFalse(optionalLeafSubstructure.isPresent());
        // but the original does
        optionalLeafSubstructure = chainToModify.getLeafSubstructure(leafIdentifier);
        assertTrue(optionalLeafSubstructure.isPresent());
    }

}