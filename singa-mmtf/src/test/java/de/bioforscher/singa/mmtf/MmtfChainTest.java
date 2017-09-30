package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
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
public class MmtfChainTest {

    private static Chain firstChain;

    @BeforeClass
    public static void prepareData() throws IOException {
        StructureDataInterface data2N5E = new GenericDecoder(ReaderUtils.getDataFromUrl("2N5E"));
        Structure structure2N5E = new MmtfStructure(data2N5E);
        firstChain = structure2N5E.getFirstChain();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals("A", firstChain.getIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() throws Exception {
        final List<LeafSubstructure> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() throws Exception {
        Optional<LeafSubstructure> leafSubstructure = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

}