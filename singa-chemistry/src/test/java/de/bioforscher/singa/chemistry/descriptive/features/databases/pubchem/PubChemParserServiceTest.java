package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class PubChemParserServiceTest {

    @Test
    public void shouldParseSpecies() {
        SmallMolecule species = PubChemParserService.parse("CID:962");
        // name
        assertEquals("water", species.getName().toLowerCase());
        // molar mass
        assertEquals(18.015, species.getFeature(MolarMass.class).getValue().doubleValue(), 0.0);
        // molar mass
        assertEquals("O", species.getFeature(Smiles.class).getFeatureContent());
        // logP
        assertEquals(-1.38, species.getFeature(LogP.class).getFeatureContent(), 0.0);
        // ChEBI identifier
        assertEquals("CHEBI:25805", species.getFeature(ChEBIIdentifier.class).getIdentifier());
        // InChIKey
        assertEquals("TUJKJAMUKRIRHC-UHFFFAOYSA-N", species.getFeature(InChIKey.class).getIdentifier());
        System.out.println(species.getStringForProtocol());
    }

    @Test
    public void shouldResolveInChIKey() {
        SmallMolecule species = new SmallMolecule.Builder("CID:5957").name("ATP").build();
        InChIKey feature = species.getFeature(InChIKey.class);
        assertEquals("ZKHQWZAMYRWXGA-KQYNXXCUSA-N", feature.getIdentifier());
    }


}