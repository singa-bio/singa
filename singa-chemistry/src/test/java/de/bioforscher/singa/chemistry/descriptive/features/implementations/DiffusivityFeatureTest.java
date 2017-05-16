package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.FeatureKind;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.WilkeCorrelation;
import de.bioforscher.singa.chemistry.descriptive.features.predictors.YoungCorrelation;
import org.junit.Assert;
import org.junit.Test;

import static de.bioforscher.singa.units.UnitProvider.SQUARE_CENTIMETER_PER_SECOND;

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
        testSpecies.assignFeature(FeatureKind.DIFFUSIVITY);
        // get feature
        Feature<?> feature = testSpecies.getFeature(FeatureKind.DIFFUSIVITY);
        // assert attributes and values
        Assert.assertEquals(FeatureKind.DIFFUSIVITY,feature.getKind());
        Assert.assertEquals(WilkeCorrelation.getInstance().getMethodName(),feature.getDescriptor().getMethodName());
        Assert.assertEquals(8.217150338823197E-6, feature.getValue(),0.0);
        Assert.assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getQuantity().getUnit());
    }

    @Test
    public void shouldUseYoungToCalculateDiffusifity() {
        Species testSpecies = new Species.Builder("heavy entity")
                .molarMass(10000)
                .build();
        // assign feature
        testSpecies.assignFeature(FeatureKind.DIFFUSIVITY);
        // get feature
        Feature<?> feature = testSpecies.getFeature(FeatureKind.DIFFUSIVITY);
        // assert attributes and values
        Assert.assertEquals(FeatureKind.DIFFUSIVITY,feature.getKind());
        Assert.assertEquals(YoungCorrelation.getInstance().getMethodName(),feature.getDescriptor().getMethodName());
        Assert.assertEquals(1.134227930559286E-6, feature.getValue(),0.0);
        Assert.assertEquals(SQUARE_CENTIMETER_PER_SECOND, feature.getQuantity().getUnit());
    }


}