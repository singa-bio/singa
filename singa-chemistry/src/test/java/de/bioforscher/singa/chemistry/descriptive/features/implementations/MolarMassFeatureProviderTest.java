package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.Protein;
import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.databases.ChEBIDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.UniProtDatabase;
import org.junit.Test;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;
import static de.bioforscher.singa.units.UnitProvider.GRAM_PER_MOLE;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class MolarMassFeatureProviderTest {

    @Test
    public void shouldUseChEBIToFetchMolarMass() {
        Species testSpecies = new Species.Builder("CHEBI:29802").build();
        // assign feature
        testSpecies.assignFeature(MOLAR_MASS);
        // get feature
        Feature<?> feature = testSpecies.getFeature(MOLAR_MASS);
        // assert attributes and values
        System.out.println(feature);
        assertEquals(MOLAR_MASS, feature.getKind());
        assertEquals(ChEBIDatabase.getInstance().getSourceName(), feature.getDescriptor().getSourceName());
        assertEquals(108.0104, feature.getValue(), 0.0);
        assertEquals(GRAM_PER_MOLE, feature.getQuantity().getUnit());
    }

    @Test
    public void shouldUseUniProtToFetchMolarMass() {
        Protein testProtein = new Protein.Builder("Q4DA54").build();
        // assign feature
        testProtein.assignFeature(MOLAR_MASS);
        // get feature
        Feature<?> feature = testProtein.getFeature(MOLAR_MASS);
        // assert attributes and values
        System.out.println(feature);
        assertEquals(MOLAR_MASS, feature.getKind());
        assertEquals(UniProtDatabase.getInstance().getSourceName(), feature.getDescriptor().getSourceName());
        assertEquals(53406.0, feature.getValue(), 0.0);
        assertEquals(GRAM_PER_MOLE, feature.getQuantity().getUnit());
    }


}
