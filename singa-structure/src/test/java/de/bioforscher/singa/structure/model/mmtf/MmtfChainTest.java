package de.bioforscher.singa.structure.model.mmtf;

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
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() throws Exception {
        final List<LeafSubstructure<?>> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() throws Exception {
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

}