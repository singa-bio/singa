package de.bioforscher.singa.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIImageService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class ChEBIParserServiceTest {

    @Test
    public void shouldParseMethanolFromChEBIOnline() {
        Species methanol = ChEBIParserService.parse("CHEBI:17790");
        assertEquals("methanol", methanol.getName().toLowerCase());
        assertEquals(32.04186, methanol.getFeature(MolarMass.class).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldFetchImageForMethanolFromChEBIDatabase() throws IOException {
        ChEBIImageService service = new ChEBIImageService("CHEBI:17790");
        assertTrue(service.parse() != null);
    }

}