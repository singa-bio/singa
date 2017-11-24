package de.bioforscher.singa.chemistry.descriptive.features.diffusivity;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import junit.framework.TestCase;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static org.junit.Assert.assertEquals;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

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

    @Test
    public void shouldScaleDiffusivityCorrectly() {
        Diffusivity diffusivity = new Diffusivity(1, FeatureOrigin.MANUALLY_ANNOTATED);
        // double time step - double the scaled quantity
        diffusivity.scale(Quantities.getQuantity(2, SECOND), Quantities.getQuantity(1, CENTI(METRE)));
        TestCase.assertEquals(2.0, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // double space step - divide by square of length
        diffusivity.scale(Quantities.getQuantity(1, SECOND), Quantities.getQuantity(2, CENTI(METRE)));
        TestCase.assertEquals(0.25, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // change both (5 * 1/(10*10))
        diffusivity.scale(Quantities.getQuantity(5, SECOND), Quantities.getQuantity(0.1, CENTI(METRE)));
        TestCase.assertEquals(500, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // some other units
        diffusivity.scale(Quantities.getQuantity(5, MILLI(SECOND)), Quantities.getQuantity(0.1, MILLI(METRE)));
        TestCase.assertEquals(50, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
    }

}