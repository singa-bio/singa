package bio.singa.chemistry.features.databases.pubchem;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.logp.LogP;
import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class PubChemParserServiceTest {

    @Test
    void shouldParseSpecies() {
        SmallMolecule species = PubChemParserService.parse("CID:962");
        // name
        assertEquals("water", species.getName().toLowerCase());
        // molar mass
        assertEquals(18.015, species.getFeature(MolarMass.class).getValue().doubleValue());
        // molar mass
        assertEquals("O", species.getFeature(Smiles.class).getFeatureContent());
        // logP
        assertEquals(-1.38, species.getFeature(LogP.class).getFeatureContent().doubleValue());
        // ChEBI identifier
        assertEquals("CHEBI:25805", species.getFeature(ChEBIIdentifier.class).getIdentifier());
        // InChIKey
        assertEquals("TUJKJAMUKRIRHC-UHFFFAOYSA-N", species.getFeature(InChIKey.class).getIdentifier());
    }

    @Test
    void shouldResolveInChIKey() {
        SmallMolecule species = new SmallMolecule.Builder("CID:5957").name("ATP").build();
        InChIKey feature = species.getFeature(InChIKey.class);
        assertEquals("ZKHQWZAMYRWXGA-KQYNXXCUSA-N", feature.getIdentifier());
    }


}