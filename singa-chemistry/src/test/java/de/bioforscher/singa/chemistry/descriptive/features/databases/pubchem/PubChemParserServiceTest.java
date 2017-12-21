package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class PubChemParserServiceTest {

    @Test
    public void shouldParseSpecies() {
        Species species = PubChemParserService.parse("CID:962");
        // name
        assertEquals("water", species.getName());
        // molar mass
        assertEquals(18.015, species.getFeature(MolarMass.class).getValue().doubleValue(), 0.0);
        // molar mass
        assertEquals("O", species.getFeature(Smiles.class).getFeatureContent());
        // logP
        assertEquals(-1.38, species.getFeature(LogP.class).getFeatureContent(), 0.0);
    }


}