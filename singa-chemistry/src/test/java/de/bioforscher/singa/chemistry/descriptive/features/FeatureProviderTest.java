package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.Species;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.DIFFUSIVITY;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class FeatureProviderTest {

    @Test
    public void shouldResolveRequiredFeature() {
        Species testSpecies = new Species.Builder("CHEBI:29802")
                .build();
        // assign feature
        // this also needs to resolve the molar mass feature
        testSpecies.assignFeature(DIFFUSIVITY);
        // get features
        Feature<?> diffusivity = testSpecies.getFeature(DIFFUSIVITY);
        Feature<?> molarMass = testSpecies.getFeature(MOLAR_MASS);
        assertEquals(DIFFUSIVITY, diffusivity.getKind());
        assertEquals(7.889770977995664E-6, diffusivity.getValue(), 0.0);
        assertEquals(MOLAR_MASS, molarMass.getKind());
        assertEquals(108.0104, molarMass.getValue(), 0.0);
    }

    @Test
    public void shouldNotCalculateTheSameFeatureTwice() {
        Species testSpecies = new Species.Builder("CHEBI:29802")
                .build();
        // this also needs to resolve the molar mass feature
        testSpecies.assignFeature(MOLAR_MASS);
        // this also needs to resolve the molar mass feature
        testSpecies.assignFeature(MOLAR_MASS);
    }


}