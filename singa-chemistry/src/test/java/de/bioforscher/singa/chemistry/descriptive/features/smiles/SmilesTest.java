package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class SmilesTest {

    @Test
    public void shouldUseChEBIToFetchSmiles() {
        Species testSpecies = new Species.Builder("CHEBI:29802").build();
        // assign feature
        testSpecies.setFeature(Smiles.class);
        // get feature
        Smiles feature = testSpecies.getFeature(Smiles.class);
        // assert attributes and values
        assertEquals("ChEBI Database", feature.getFeatureOrigin().getName());
        assertEquals("[O-][N+](=O)O[N+]([O-])=O", feature.getFeatureContent());
    }

}