package de.bioforscher.singa.structure.parser.sifts;

import de.bioforscher.singa.features.identifiers.PfamIdentifier;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PDBPfamMapperTest {

    @Test
    public void shouldMapChainsToPfam() {
        Map<String, PfamIdentifier> map = PDBPfamMapper.map("1c0a");
        assertEquals("PF02938", map.get("A").getIdentifier());
    }
}