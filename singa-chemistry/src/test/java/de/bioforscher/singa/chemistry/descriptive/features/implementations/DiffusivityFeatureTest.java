package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.WilkeCorrelation;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.YoungCorrelation;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.DIFFUSIVITY;
import static de.bioforscher.singa.units.UnitProvider.SQUARE_CENTIMETER_PER_SECOND;
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
        testSpecies.assignFeature(DIFFUSIVITY);
        // get feature
        Feature<?> feature = testSpecies.getFeature(DIFFUSIVITY);
        // assert attributes and values
        assertEquals(DIFFUSIVITY,feature.getKind());
        assertEquals(WilkeCorrelation.getInstance().getSourceName(),feature.getDescriptor().getSourceName());
        assertEquals(8.217150338823197E-6, feature.getValue(),0.0);
        assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getQuantity().getUnit());
    }

    @Test
    public void shouldUseYoungToCalculateDiffusifity() {
        Species testSpecies = new Species.Builder("heavy entity")
                .molarMass(10000)
                .build();
        // assign feature
        testSpecies.assignFeature(DIFFUSIVITY);
        // get feature
        Feature<?> feature = testSpecies.getFeature(DIFFUSIVITY);
        // assert attributes and values
        assertEquals(DIFFUSIVITY,feature.getKind());
        assertEquals(YoungCorrelation.getInstance().getSourceName(),feature.getDescriptor().getSourceName());
        assertEquals(1.134227930559286E-6, feature.getValue(),0.0);
        assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getQuantity().getUnit());
    }


}