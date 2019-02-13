package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class LogPTest {

    @Test
    void shouldUsePubChemToFetchLogP() {
        SmallMolecule testSpecies = SmallMolecule.create("CID:5957").build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(-5.5, feature.getContent().doubleValue());
    }

    @Test
    void shouldBeAbleToFetchLogPWithChEBISpecies() {
        SmallMolecule testSpecies = ChEBIParserService.parse("CHEBI:8772");
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(5.2, feature.getContent().doubleValue());
    }

}