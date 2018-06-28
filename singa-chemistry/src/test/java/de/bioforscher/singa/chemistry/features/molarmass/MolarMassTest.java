package de.bioforscher.singa.chemistry.features.molarmass;

import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;

import static de.bioforscher.singa.structure.features.molarmass.MolarMass.GRAM_PER_MOLE;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class MolarMassTest {

    @Test
    public void shouldUseChEBIToFetchMolarMass() {
        SmallMolecule testSpecies = new SmallMolecule.Builder("CHEBI:29802").build();
        // get feature
        MolarMass feature = testSpecies.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("ChEBI Database", feature.getFeatureOrigin().getName());
        assertEquals(108.0104, feature.getValue().doubleValue(), 0.0);
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }

    @Test
    public void shouldUseUniProtToFetchMolarMass() {
        Protein testProtein = new Protein.Builder("Q4DA54").build();
        // get feature
        MolarMass feature = testProtein.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("UniProt Database", feature.getFeatureOrigin().getName());
        assertEquals(53406.0, feature.getValue().doubleValue(), 0.0);
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }


}
