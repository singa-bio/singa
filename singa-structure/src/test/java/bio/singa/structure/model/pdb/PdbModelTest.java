package bio.singa.structure.model.pdb;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class PdbModelTest {

    private static PdbModel firstModel;
    private static PdbModel secondModel;
    private static PdbModel modelToModify;
    private static Model anotherModelToModify;

    @BeforeAll
    static void prepareData() {
        Structure structure2N5E = StructureParser.pdb().pdbIdentifier("2N5E").parse();
        firstModel = (PdbModel) structure2N5E.getFirstModel();
        secondModel = (PdbModel) structure2N5E.getModel(2).get();
        modelToModify = (PdbModel) structure2N5E.getModel(3).get();
        anotherModelToModify = structure2N5E.getModel(4).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(1, (int) firstModel.getModelIdentifier());
        assertEquals(2, (int) secondModel.getModelIdentifier());
    }

    @Test
    void getAllChains() {
        final Collection<PdbChain> allChains = firstModel.getAllChains();
        assertEquals(2, allChains.size());
    }

    @Test
    void getFirstChain() {
        final Chain firstChain = firstModel.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getChain() {
        final Optional<PdbChain> chain = firstModel.getChain("B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        final List<PdbLeafSubstructure> leafSubstructures = secondModel.getAllLeafSubstructures();
        assertEquals(334, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<PdbLeafSubstructure> leafSubstructure = firstModel.getLeafSubstructure(new PdbLeafIdentifier("2N5E", 1, "B", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }


    @Test
    void addChain() {
        final int expected = modelToModify.getAllChains().size() + 1;
        modelToModify.addChain(new PdbChain("C"));
        final int actual = modelToModify.getAllChains().size();
        assertEquals(expected, actual);
    }


    @Test
    void removeLeafSubstructure() {
        final int expected = modelToModify.getNumberOfLeafSubstructures() - 1;
        final boolean response = modelToModify.removeLeafSubstructure(new PdbLeafIdentifier("2N5E", 3, "B", 64));
        if (!response) {
            fail("Response was false but should be true if any leaf substructure was removed.");
        }
        final int actual = modelToModify.getNumberOfLeafSubstructures();
        assertEquals(expected, actual);
    }

    @Test
    void getAtom() {
        // ATOM     17  OG1 THR A  56       5.624   2.561  -0.853  1.00  0.00           O
        final Optional<PdbAtom> atom = secondModel.getAtom(17);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("OG1", atom.get().getAtomName());
        assertEquals(new Vector3D(5.624, 2.561, -0.853), atom.get().getPosition());
    }

    @Test
    void removeAtom() {
        final int expected = modelToModify.getAllAtoms().size() - 1;
        modelToModify.removeAtom(17);
        final int actual = modelToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    void getCopy() {
        final PdbModel firstModelCopy = firstModel.getCopy();
        assertNotSame(firstModel, firstModelCopy);
        assertEquals(firstModel, firstModelCopy);
    }

    @Test
    void getAllChainIdentifiers() {
        int actual = firstModel.getAllChainIdentifiers().size();
        assertEquals(2, actual);
    }

    @Test
    void removeChain() {
        final int expectedChains = anotherModelToModify.getAllChainIdentifiers().size() - 1;
        final int expectedLeafs = anotherModelToModify.getNumberOfLeafSubstructures() - 167;
        anotherModelToModify.removeChain("A");
        final int actualChains = anotherModelToModify.getAllChainIdentifiers().size();
        final int actualLeafs = anotherModelToModify.getNumberOfLeafSubstructures();
        assertEquals(expectedChains, actualChains);
        assertEquals(expectedLeafs, actualLeafs);
    }

}