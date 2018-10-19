package bio.singa.chemistry.features.diffusivity;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class DiffusivityTest {

    @Test
    public void shouldResolveRequiredFeature() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("dnp")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:29802"))
                .build();
        Diffusivity diffusivity = testSpecies.getFeature(Diffusivity.class);
        // this also needs to resolve the molar mass feature
        MolarMass molarMass = testSpecies.getFeature(MolarMass.class);
        assertEquals(7.889770977995664E-6, diffusivity.getValue().doubleValue(), 0.0);
        assertEquals(108.0104, molarMass.getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldUseWilkeToCalculateDiffusifity() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("light entity")
                .assignFeature(new MolarMass(100, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();
        // get feature
        Diffusivity feature = testSpecies.getFeature(Diffusivity.class);
        // assert attributes and values
        assertEquals("Wilke Correlation", feature.getFeatureOrigin().getName());
        assertEquals(8.217150338823197E-6, feature.getValue().doubleValue(), 0.0);
        Assert.assertEquals(Diffusivity.SQUARE_CENTIMETRE_PER_SECOND, feature.getUnit());
    }

    @Test
    public void shouldUseYoungToCalculateDiffusifity() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("heavy entity")
                .assignFeature(new MolarMass(10000, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();
        // get feature
        Diffusivity feature = testSpecies.getFeature(Diffusivity.class);
        // assert attributes and values
        assertEquals("Young Correlation", feature.getFeatureOrigin().getName());
        assertEquals(1.134227930559286E-6, feature.getValue().doubleValue(), 0.0);
        Assert.assertEquals(Diffusivity.SQUARE_CENTIMETRE_PER_SECOND, feature.getUnit());
    }

}