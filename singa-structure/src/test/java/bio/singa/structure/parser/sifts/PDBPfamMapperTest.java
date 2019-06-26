package bio.singa.structure.parser.sifts;

import bio.singa.features.identifiers.PfamIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDBPfamMapperTest {

    @Test
    @Disabled("Sifts is currently broken. Skipped test")
    void shouldMapChainsToPfam() {
        Map<String, PfamIdentifier> map = PDBPfamMapper.map("1c0a");
        assertEquals("PF02938", map.get("A").getContent());
    }
}