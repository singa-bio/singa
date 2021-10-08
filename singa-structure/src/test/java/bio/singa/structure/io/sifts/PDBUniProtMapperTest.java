package bio.singa.structure.io.sifts;

import bio.singa.features.identifiers.UniProtIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class PDBUniProtMapperTest {

    @Test
    void shouldMapChainsToUniProt() {
        Map<String, UniProtIdentifier> map = PDBUniProtMapper.map("4hhb");
        assertEquals("P69905", map.get("A").getContent());
        assertEquals("P68871", map.get("B").getContent());
        assertEquals("P69905", map.get("C").getContent());
        assertEquals("P68871", map.get("D").getContent());
    }

}