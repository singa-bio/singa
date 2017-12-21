package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class ChEBIParserServiceTest {

    @Test
    @Ignore
    public void shouldParseMethanolFromChEBIOnline() {
        // this is a known problem and fixed in another branch
        Species methanol = ChEBIParserService.parse("CHEBI:17790");
        assertEquals("methanol", methanol.getName().toLowerCase());
        assertEquals(32.04186, methanol.getFeature(MolarMass.class).getValue().doubleValue(), 0.0);
    }

    @Test
    @Ignore
    public void shouldSearchMethanolInChEBIDatabase() {
        // this is a known problem and fixed in another branch
        ChEBISearchService service = new ChEBISearchService();
        service.setSearchTerm("Methanol");
        List<Species> searchResult = service.search();
        assertEquals(20, searchResult.size());
        for (Species species : searchResult) {
            assertTrue(species != null);
        }
    }

    @Test
    public void shouldFetchImageForMethanolFromChEBIDatabase() throws IOException {
        ChEBIImageService service = new ChEBIImageService("CHEBI:17790");
        assertTrue(service.parse() != null);
    }

}