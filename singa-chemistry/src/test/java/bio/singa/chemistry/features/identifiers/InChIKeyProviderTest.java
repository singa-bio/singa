package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.identifiers.InChIKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class InChIKeyProviderTest {

    private static ChemicalEntity chebiEntity;
    private static ChemicalEntity pubchemEntity;

    @BeforeAll
    static void initialize() {
        // salicin
        chebiEntity = SmallMolecule.create("CHEBI:17814").build();
        // epoprostenol
        pubchemEntity = SmallMolecule.create("CID:5282411").build();
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