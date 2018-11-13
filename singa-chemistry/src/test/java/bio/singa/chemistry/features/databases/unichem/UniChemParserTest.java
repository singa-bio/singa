package bio.singa.chemistry.features.databases.unichem;

import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PDBLigandIdentifier;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class UniChemParserTest {

    private static List<Identifier> identifiers;

    @BeforeAll
    static void initialize() {
        identifiers = UniChemParser.parse(new InChIKey("GZUITABIAKMVPG-UHFFFAOYSA-N"));
    }

    @Test
    @DisplayName("parse unichem - chebi identifier")
    void fetchChEBIIdentifier() {
        assertTrue(identifiers.contains(new ChEBIIdentifier("CHEBI:8772")));
    }

    @Test
    @DisplayName("parse unichem - pubchem identifier")
    void fetchPubChemIdentifier() {
        assertTrue(identifiers.contains(new PubChemIdentifier("CID:5035")));
    }

    @Test
    @DisplayName("parse unichem - pdb ligand identifier")
    void fetchPDBLigandIdentifier() {
        assertTrue(identifiers.contains(new PDBLigandIdentifier("RAL")));
    }

}