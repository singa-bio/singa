package bio.singa.chemistry.features.molarmass;

import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.quantities.MolarMass;
import org.junit.jupiter.api.Test;

import static bio.singa.features.quantities.MolarMass.GRAM_PER_MOLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class MolarMassTest {

    @Test
    void shouldUseChEBIToFetchMolarMass() {
        SmallMolecule testSpecies = SmallMolecule.create("CHEBI:29802")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:29802"))
                .build();
        // get feature
        MolarMass feature = testSpecies.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("ChEBI Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(108.0104, feature.getValue().doubleValue());
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }

}
