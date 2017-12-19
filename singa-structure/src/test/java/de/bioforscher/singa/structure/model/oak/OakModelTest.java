package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class OakModelTest {

    private static OakModel firstModel;
    private static OakModel secondModel;
    private static OakModel modelToModify;

    @BeforeClass
    public static void prepareData() throws IOException {
        Structure structure2N5E = StructureParser.online().pdbIdentifier("2N5E").parse();
        firstModel = (OakModel) structure2N5E.getFirstModel();
        secondModel = (OakModel) structure2N5E.getModel(2).get();
        modelToModify = (OakModel) structure2N5E.getModel(3).get();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals(1, (int) firstModel.getModelIdentifier());
        assertEquals(2, (int) secondModel.getModelIdentifier());
    }

    @Test
    public void getAllChains() throws Exception {
        final List<Chain> allChains = firstModel.getAllChains();
        assertEquals(2, allChains.size());
    }

    @Test
    public void getFirstChain() throws Exception {
        final Chain firstChain = firstModel.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    public void getChain() throws Exception {
        final Optional<Chain> chain = firstModel.getChain("B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() throws Exception {
        final List<LeafSubstructure<?>> leafSubstructures = secondModel.getAllLeafSubstructures();
        assertEquals(334, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() throws Exception {
        Optional<LeafSubstructure<?>> leafSubstructure = firstModel.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "B", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }


    @Test
    public void addChain() throws Exception {
        final int expected = modelToModify.getAllChains().size() + 1;
        modelToModify.addChain(new OakChain("C"));
        final int actual = modelToModify.getAllChains().size();
        assertEquals(expected, actual);
    }


    @Test
    public void removeLeafSubstructure() throws Exception {
        final int expected = modelToModify.getAllLeafSubstructures().size() - 1;
        final boolean response = modelToModify.removeLeafSubstructure(new LeafIdentifier("2N5E", 3, "B", 64));
        if (!response) {
            fail("Response was false but should be true if any leaf substructure was removed.");
        }
        final int actual = modelToModify.getAllLeafSubstructures().size();
        assertEquals(expected, actual);
    }

    @Test
    public void getAtom() throws Exception {
        // ATOM     17  OG1 THR A  56       5.624   2.561  -0.853  1.00  0.00           O
        final Optional<Atom> atom = secondModel.getAtom(17);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("OG1", atom.get().getAtomName());
        assertEquals(new Vector3D(5.624, 2.561, -0.853), atom.get().getPosition());
    }

    @Test
    public void removeAtom() throws Exception {
        final int expected = modelToModify.getAllAtoms().size() - 1;
        modelToModify.removeAtom(17);
        final int actual = modelToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    public void getCopy() throws Exception {
        final OakModel firstModelCopy = firstModel.getCopy();
        assertTrue(firstModel != firstModelCopy);
        assertTrue(firstModel.equals(firstModelCopy));
    }

}