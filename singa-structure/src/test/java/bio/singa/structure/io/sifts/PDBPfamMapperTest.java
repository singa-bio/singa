package bio.singa.structure.io.sifts;

import bio.singa.features.identifiers.PfamIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDBPfamMapperTest {

    @Test
    void shouldMapChainsToPfam() {
        Map<String, PfamIdentifier> map = PDBPfamMapper.map("1c0a");
        // TODO there can be any number of Pfams per chain, 1c0a.A has 3: PF02938, PF00152, PF01336 (https://www.rcsb.org/annotations/1C0A#pfam)
        assertEquals("PF00152", map.get("A").getContent());
    }
}