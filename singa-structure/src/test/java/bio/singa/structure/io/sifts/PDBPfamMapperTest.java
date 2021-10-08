package bio.singa.structure.io.sifts;

import bio.singa.features.identifiers.PfamIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDBPfamMapperTest {

    @Test
    void shouldMapChainsToPfam() {
        Map<String, PfamIdentifier> map = PDBPfamMapper.map("1c0a");
        assertEquals("PF02938", map.get("A").getContent());
    }
}