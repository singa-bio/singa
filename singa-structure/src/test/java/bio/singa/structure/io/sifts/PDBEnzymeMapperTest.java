package bio.singa.structure.io.sifts;

import bio.singa.features.identifiers.ECNumber;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDBEnzymeMapperTest {

    @Test
    void shouldMapChainsToEC() {
        Map<String, ECNumber> map = PDBEnzymeMapper.map("1c0a");
        assertEquals("6.1.1.12", map.get("A").getContent());
    }
}