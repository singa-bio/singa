package bio.singa.chemistry.features.molarmass;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.Test;

import static bio.singa.structure.features.molarmass.MolarMass.GRAM_PER_MOLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class MolarMassTest {

    @Test
    void shouldUseChEBIToFetchMolarMass() {
        SmallMolecule testSpecies = SmallMolecule.create("CHEBI:29802").build();
        // get feature
        MolarMass feature = testSpecies.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("ChEBI Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(108.0104, feature.getValue().doubleValue());
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }

    @Test
    void shouldUseUniProtToFetchMolarMass() {
        Protein testProtein = new Protein.Builder("Q4DA54").build();
        // get feature
        MolarMass feature = testProtein.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("UniProt Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(53406.0, feature.getValue().doubleValue());
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }


}
