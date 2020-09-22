package bio.singa.chemistry.features.databases.unichem;

import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PDBLigandIdentifier;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @Disabled
    void fetchChEBIIdentifier() {
        for (Identifier identifier : identifiers) {
            if (identifier instanceof ChEBIIdentifier) {
                assertEquals("CHEBI:8772", identifier.getContent());
                return;
            }
        }
        fail("ChEBIIdentifier not found");
    }

    @Test
    @DisplayName("parse unichem - pubchem identifier")
    @Disabled
    void fetchPubChemIdentifier() {
        for (Identifier identifier : identifiers) {
            if (identifier instanceof PubChemIdentifier) {
                assertEquals("CID:5035", identifier.getContent());
                return;
            }
        }
        fail("PubChemIdentifier not found");
    }

    @Test
    @DisplayName("parse unichem - pdb ligand identifier")
    @Disabled
    void fetchPDBLigandIdentifier() {
        for (Identifier identifier : identifiers) {
            if (identifier instanceof PDBLigandIdentifier) {
                assertEquals("RAL", identifier.getContent());
                return;
            }
        }
        fail("PDBLigandIdentifier not found");
    }

}