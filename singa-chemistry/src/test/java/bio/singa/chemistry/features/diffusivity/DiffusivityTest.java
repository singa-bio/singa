package bio.singa.chemistry.features.diffusivity;

import bio.singa.chemistry.features.FeatureProviderRegistry;
import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.quantities.ConcentrationDiffusivity;
import bio.singa.features.quantities.Diffusivity;
import bio.singa.features.quantities.MolarMass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class DiffusivityTest {

    @BeforeAll
    static void initialize() {
        FeatureProviderRegistry.addProviderForFeature(ConcentrationDiffusivity.class, DiffusivityProvider.class);
    }

    @Test
    void shouldResolveRequiredFeature() {
        SmallMolecule testSpecies = SmallMolecule.create("dnp")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:29802"))
                .build();
        ConcentrationDiffusivity diffusivity = testSpecies.getFeature(ConcentrationDiffusivity.class);
        // this also needs to resolve the molar mass feature
        MolarMass molarMass = testSpecies.getFeature(MolarMass.class);
        assertEquals(108.0104, molarMass.getValue().doubleValue());
        assertEquals(7.889770977995664E-6, diffusivity.getContent().getValue().doubleValue());
    }

    @Test
    void shouldUseWilkeToCalculateDiffusifity() {
        SmallMolecule testSpecies = SmallMolecule.create("light entity")
                .assignFeature(MolarMass.of(100, MolarMass.GRAM_PER_MOLE).build())
                .build();
        // get feature
        ConcentrationDiffusivity feature = testSpecies.getFeature(ConcentrationDiffusivity.class);
        // assert attributes and values
        assertEquals("Wilke 1955", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(8.217150338823197E-6, feature.getContent().getValue().doubleValue());
        assertEquals(Diffusivity.SQUARE_CENTIMETRE_PER_SECOND, feature.getContent().getUnit());
    }

    @Test
    void shouldUseYoungToCalculateDiffusifity() {
        SmallMolecule testSpecies = SmallMolecule.create("heavy entity")
                .assignFeature(MolarMass.of(10000, MolarMass.GRAM_PER_MOLE).build())
                .build();
        // get feature
        ConcentrationDiffusivity feature = testSpecies.getFeature(ConcentrationDiffusivity.class);
        // assert attributes and values
        assertEquals("Young 1980", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(Diffusivity.SQUARE_CENTIMETRE_PER_SECOND, feature.getContent().getUnit());
        assertEquals(1.134227930559286E-6, feature.getContent().getValue().doubleValue());
    }

}