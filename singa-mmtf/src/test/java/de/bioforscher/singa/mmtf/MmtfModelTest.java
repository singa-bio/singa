package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.interfaces.Model;
import de.bioforscher.singa.chemistry.physical.interfaces.Structure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author cl
 */
public class MmtfModelTest {

    private static Model firstModel;
    private static Model secondModel;

    @BeforeClass
    public static void prepareData() throws IOException {
        StructureDataInterface data2N5E = new GenericDecoder(ReaderUtils.getDataFromUrl("2N5E"));
        Structure structure2N5E = new MmtfStructure(data2N5E);
        firstModel = structure2N5E.getFirstModel();
        secondModel = structure2N5E.getModel(2).get();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals(1, firstModel.getIdentifier());
        assertEquals(2, secondModel.getIdentifier());
    }

    @Test
    public void getAllChains() throws Exception {
        final List<Chain> allChains = firstModel.getAllChains();
        assertEquals(2, allChains.size());
    }

    @Test
    public void getFirstChain() throws Exception {
        final Chain firstChain = firstModel.getFirstChain();
        assertEquals("A", firstChain.getIdentifier());
    }

    @Test
    public void getChain() throws Exception {
        final Optional<Chain> chain = firstModel.getChain("B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getIdentifier());
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

}