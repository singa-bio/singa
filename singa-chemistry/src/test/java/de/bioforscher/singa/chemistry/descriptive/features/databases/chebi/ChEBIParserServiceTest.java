package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class ChEBIParserServiceTest {

    @Test
    public void shouldParseMethanolFromChEBIOnline() {
        SmallMolecule methanol = ChEBIParserService.parse("CHEBI:17790");
        assertEquals("methanol", methanol.getName().toLowerCase());
        assertEquals(32.04186, methanol.getFeature(MolarMass.class).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldFetchImageForMethanolFromChEBIDatabase() {
        ChEBIImageService service = new ChEBIImageService("CHEBI:17790");
        assertTrue(service.parse() != null);
    }

}