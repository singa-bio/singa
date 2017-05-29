package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import org.junit.Test;

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
        testSpecies.setFeature(Diffusivity.class);
        // get features
        Diffusivity diffusivity = testSpecies.getFeature(Diffusivity.class);
        MolarMass molarMass = testSpecies.getFeature(MolarMass.class);
        assertEquals(7.889770977995664E-6, diffusivity.getValue().doubleValue(), 0.0);
        assertEquals(108.0104, molarMass.getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldNotCalculateTheSameFeatureTwice() {
        Species testSpecies = new Species.Builder("CHEBI:29802")
                .build();
        // this also needs to resolve the molar mass feature
        testSpecies.setFeature(MolarMass.class);
        // this also needs to resolve the molar mass feature
        testSpecies.setFeature(MolarMass.class);
    }


}