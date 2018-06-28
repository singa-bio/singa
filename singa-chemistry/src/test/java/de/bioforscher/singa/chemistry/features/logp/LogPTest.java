package de.bioforscher.singa.chemistry.features.logp;

import de.bioforscher.singa.chemistry.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.features.databases.chebi.ChEBIParserService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class LogPTest {

    @Test
    public void shouldUsePubChemToFetchLogP() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("CID:5957").build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getFeatureOrigin().getName());
        assertEquals(-5.5, feature.getFeatureContent(), 0.0);
    }

    @Test
    public void shouldBeAbleToFetchLogPWithChEBISpecies() {
        SmallMolecule testSpecies = ChEBIParserService.parse("CHEBI:8772");
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getFeatureOrigin().getName());
        assertEquals(5.2, feature.getFeatureContent(), 0.0);
    }

}