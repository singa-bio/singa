package de.bioforscher.singa.chemistry.descriptive.features.diffusivity;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.features.model.FeatureOrigin;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class DiffusivityTest {

    @Test
    public void shouldResolveRequiredFeature() {
        Species testSpecies = new Species.Builder("CHEBI:29802")
                .build();
        // assign feature
        // this also needs to resolve the molar mass feature
        testSpecies.setFeature(Diffusivity.class);
        // get features
        Diffusivity diffusivity = testSpecies.getFeature(Diffusivity.class);
        MolarMass molarMass = testSpecies.getFeature(MolarMass.class);
        assertEquals(7.889770977995664E-6, diffusivity.getValue().doubleValue(), 0.0);
        assertEquals(108.0104, molarMass.getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldUseWilkeToCalculateDiffusifity() {
        Species testSpecies = new Species.Builder("light entity")
                .assignFeature(new MolarMass(100, FeatureOrigin.MANUALLY_ANNOTATED))
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
                .assignFeature(new MolarMass(10000, FeatureOrigin.MANUALLY_ANNOTATED))
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