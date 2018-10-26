package bio.singa.structure.parser.sifts;

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
        assertEquals("P69905", map.get("A").getIdentifier());
        assertEquals("P68871", map.get("B").getIdentifier());
        assertEquals("P69905", map.get("C").getIdentifier());
        assertEquals("P68871", map.get("D").getIdentifier());
    }

}