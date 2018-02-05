package de.bioforscher.singa.structure.parser.sifts;

import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class PDBUniProtMapperTest {

    @Test
    public void shouldMapChainsToUniProt() {
        Map<String, UniProtIdentifier> map = PDBUniProtMapper.map("4hhb");
        assertEquals("P69905", map.get("A").getIdentifier());
        assertEquals("P68871", map.get("B").getIdentifier());
        assertEquals("P69905", map.get("C").getIdentifier());
        assertEquals("P68871", map.get("D").getIdentifier());
    }

}