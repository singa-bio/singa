package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class DiffusivityFeatureTest {

    @Test
    public void shouldUseWilkeToCalculateDiffusifity() {
        Species testSpecies = new Species.Builder("light entity")
                .molarMass(100)
                .build();
        // assign feature
        testSpecies.setFeature(Diffusivity.class);
        // get feature
        Diffusivity feature = testSpecies.getFeature(Diffusivity.class);
        // assert attributes and values
        assertEquals("Wilke Correlation", feature.getFeatureOrigin().getName());
        assertEquals(8.217150338823197E-6, feature.getValue().doubleValue(),0.0);
        assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getUnit());
    }

    @Test
    public void shouldUseYoungToCalculateDiffusifity() {
        Species testSpecies = new Species.Builder("heavy entity")
                .molarMass(10000)
                .build();
        // assign feature
        testSpecies.setFeature(Diffusivity.class);
        // get feature
        Diffusivity feature = testSpecies.getFeature(Diffusivity.class);
        // assert attributes and values
        assertEquals("Young Correlation",feature.getFeatureOrigin().getName());
        assertEquals(1.134227930559286E-6, feature.getValue().doubleValue(),0.0);
        assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getUnit());
    }


}