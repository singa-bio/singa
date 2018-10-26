package bio.singa.chemistry.features.databases.unichem;

import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class UniChemParserTest {

    @Test
    void shouldFetchIdentifier() {
        InChIKey key = new InChIKey("GZUITABIAKMVPG-UHFFFAOYSA-N");
        List<Identifier> identifiers = UniChemParser.parse(key);
        assertTrue(identifiers.contains(new ChEBIIdentifier("CHEBI:8772")));
        assertTrue(identifiers.contains(new PubChemIdentifier("CID:5035")));
    }

}