package bio.singa.structure.model.mmtf;

import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author cl
 */
class MmtfModelTest {

    private static Model firstModel;
    private static Model secondModel;
    private static Model modelToModify;

    @BeforeAll
    static void initialize() {
        Structure structure2N5E = StructureParser.mmtf()
                .pdbIdentifier("2N5E")
                .everything().parse();
        firstModel = structure2N5E.getFirstModel();
        secondModel = structure2N5E.getModel(2).get();
        modelToModify = structure2N5E.getModel(3).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(1, (int) firstModel.getModelIdentifier());
        assertEquals(2, (int) secondModel.getModelIdentifier());
    }

    @Test
    void getAllChains() {
        final Collection<? extends Chain> allChains = firstModel.getAllChains();
        assertEquals(2, allChains.size());
    }

    @Test
    void getFirstChain() {
        final Chain firstChain = firstModel.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getChain() {
        final Optional<? extends Chain> chain = firstModel.getChain("B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        final Collection<? extends LeafSubstructure> leafSubstructures = secondModel.getAllLeafSubstructures();
        assertEquals(334, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<? extends LeafSubstructure> leafSubstructure = firstModel.getLeafSubstructure(new PdbLeafIdentifier("2N5E", 1, "B", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

    @Test
    void getAllChainIdentifiers() {
        Set<String> allChainIdentifiers = secondModel.getAllChainIdentifiers();
        assertEquals(allChainIdentifiers.size(), 2);
    }

    @Test
    void removeChain() {
        final int expectedChains = modelToModify.getAllChainIdentifiers().size() - 1;
        final int expectedLeafs = modelToModify.getNumberOfLeafSubstructures() - 167;
        modelToModify.removeChain("A");
        final int actualChains = modelToModify.getAllChainIdentifiers().size();
        final int actualLeafs = modelToModify.getNumberOfLeafSubstructures();
        assertEquals(expectedChains, actualChains);
        assertEquals(expectedLeafs, actualLeafs);
    }
}