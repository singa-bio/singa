package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class InChIKeyProviderTest {

    private static SmallMolecule chebiEntity;
    private static SmallMolecule pubchemEntity;

    @BeforeAll
    static void initialize() {
        // salicin
        chebiEntity = SmallMolecule.create("CHEBI:17814")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17814"))
                .build();
        // epoprostenol
        pubchemEntity = SmallMolecule.create("CID:5282411")
                .additionalIdentifier(new PubChemIdentifier("CID:5282411"))
                .build();
    }

    @Test
    @DisplayName("inchi key provider - using chebi identifier")
    void fetchWithChebi() {
        chebiEntity.setFeature(InChIKey.class);
        assertEquals("NGFMICBWJRZIBI-UJPOAAIJSA-N", chebiEntity.getFeature(InChIKey.class).toString());
    }

    @Test
    @DisplayName("inchi key provider - using pubchem identifier")
    void fetchWithPubChem() {
        pubchemEntity.setFeature(InChIKey.class);
        assertEquals("KAQKFAOMNZTLHT-OZUDYXHBSA-N", pubchemEntity.getFeature(InChIKey.class).toString());
    }

}