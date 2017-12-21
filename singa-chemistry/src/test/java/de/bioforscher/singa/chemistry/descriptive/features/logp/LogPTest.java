package de.bioforscher.singa.chemistry.descriptive.features.logp;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class LogPTest {

    @Test
    public void shouldUsePubChemToFetchLogP() {
        Species testSpecies = new Species.Builder("CID:5957").build();
        // assign feature
        testSpecies.setFeature(LogP.class);
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getFeatureOrigin().getName());
        assertEquals(-5.5, feature.getFeatureContent(), 0.0);
    }

    @Test
    public void shouldBeAbleToFetchLogPWithChEBISpecies() {
        Species testSpecies = ChEBIParserService.parse("CHEBI:8772");
        // assign feature
        testSpecies.setFeature(LogP.class);
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getFeatureOrigin().getName());
        assertEquals(5.2, feature.getFeatureContent(), 0.0);
    }

}