package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.PubChemIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author cl
 */
class LogPTest {

    @Test
    @Disabled
    void shouldUsePubChemToFetchLogP() {
        SmallMolecule testSpecies = SmallMolecule.create("CID:5957")
                .additionalIdentifier(new PubChemIdentifier("CID:5957"))
                .build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(-5.5, feature.getContent().doubleValue());
    }

    @Test
    @Disabled
    void shouldFailToFetchLogP() {
        SmallMolecule testSpecies = SmallMolecule.create("Test").build();
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertNull(feature);
    }

}