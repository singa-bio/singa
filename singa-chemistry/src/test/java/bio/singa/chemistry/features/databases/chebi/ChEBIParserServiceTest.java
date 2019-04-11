package bio.singa.chemistry.features.databases.chebi;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class ChEBIParserServiceTest {

    @Test
    void shouldParseMethanolFromChEBIOnline() {
        SmallMolecule methanol = ChEBIParserService.parse("CHEBI:17790");
        assertEquals("methanol", methanol.getNames().iterator().next().toLowerCase());
        assertEquals(32.04186, methanol.getFeature(MolarMass.class).getValue().doubleValue());
    }

    @Test
    void shouldFetchImageForMethanolFromChEBIDatabase() {
        ChEBIImageService service = new ChEBIImageService("CHEBI:17790");
        assertNotNull(service.parse());
    }

}