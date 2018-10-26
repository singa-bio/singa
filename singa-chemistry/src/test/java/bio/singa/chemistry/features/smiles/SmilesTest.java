package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.entities.SmallMolecule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class SmilesTest {

    @Test
    void shouldUseChEBIToFetchMolarMass() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("CHEBI:29802").build();
        // assign feature
        testSpecies.setFeature(Smiles.class);
        // get feature
        Smiles feature = testSpecies.getFeature(Smiles.class);
        // assert attributes and values
        assertEquals("ChEBI Database", feature.getFeatureOrigin().getName());
        assertEquals("[O-][N+](=O)O[N+]([O-])=O", feature.getFeatureContent());
    }

}