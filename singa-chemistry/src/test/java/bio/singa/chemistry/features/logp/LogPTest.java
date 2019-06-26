package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class LogPTest {

    @Test
    @Disabled
    void shouldUsePubChemToFetchLogP() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("CID:5957").build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getEvidence().getName());
        assertEquals(-5.5, feature.getFeatureContent().doubleValue());
    }

    @Test
    @Disabled
    void shouldBeAbleToFetchLogPWithChEBISpecies() {
        SmallMolecule testSpecies = ChEBIParserService.parse("CHEBI:8772");
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getEvidence().getName());
        assertEquals(5.2, feature.getFeatureContent().doubleValue());
    }

}