package de.bioforscher.singa.structure.parser.sifts;

import de.bioforscher.singa.core.identifier.ECNumber;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PDBEnzymeMapperTest {

    @Test
    public void shouldMapChainsToPfam() {
        Map<String, ECNumber> map = PDBEnzymeMapper.map("1c0a");
        System.out.println(map);
        assertEquals("6.1.1.12", map.get("A").getIdentifier());
    }
}