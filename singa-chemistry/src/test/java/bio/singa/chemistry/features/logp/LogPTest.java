package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import bio.singa.features.identifiers.PubChemIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author cl
 */
class LogPTest {

    @Test
    void shouldUsePubChemToFetchLogP() {
        SmallMolecule testSpecies = SmallMolecule.create("CID:5957")
                .additionalIdentifier(new PubChemIdentifier("CID:5957"))
                .build();
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

    @Test
    void shouldFailToFetchLogP() {
        SmallMolecule testSpecies = SmallMolecule.create("Test").build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertNull(feature);
    }

}