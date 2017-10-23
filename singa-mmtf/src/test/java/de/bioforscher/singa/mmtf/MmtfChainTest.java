package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author cl
 */
public class MmtfChainTest {

    private static Chain firstChain;

    @BeforeClass
    public static void prepareData() throws IOException {
        Structure structure2N5E = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("2N5E"));
        firstChain = structure2N5E.getFirstChain();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals("A", firstChain.getIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() throws Exception {
        final List<LeafSubstructure<?>> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() throws Exception {
        Optional<LeafSubstructure<?>> leafSubstructure = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

}