package de.bioforscher.singa.chemistry.parser;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBIImageService;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBISearchService;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class SpeciesParserTest {

    @Test
    public void shouldParseMethanolFromChEBIOnline() {
        ChEBIParserService parser = new ChEBIParserService("CHEBI:17790");
        Species methanol = parser.fetchSpecies();
        assertEquals("methanol", methanol.getName().toLowerCase());
        assertEquals(32.04186, methanol.getMolarMass().getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldSearchMethanolInChEBIDatabase() {
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
        service.fetchResource();
        assertTrue(service.getImageStream() != null);
    }
}