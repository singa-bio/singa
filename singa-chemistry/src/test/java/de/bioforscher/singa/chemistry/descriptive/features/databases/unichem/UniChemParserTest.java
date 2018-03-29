package de.bioforscher.singa.chemistry.descriptive.features.databases.unichem;

import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.identifiers.model.Identifier;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class UniChemParserTest {

    @Test
    public void shouldFetchIdentifier() {
        InChIKey key = new InChIKey("GZUITABIAKMVPG-UHFFFAOYSA-N");
        List<Identifier> identifiers = UniChemParser.parse(key);
        assertTrue(identifiers.contains(new ChEBIIdentifier("CHEBI:8772")));
        assertTrue(identifiers.contains(new PubChemIdentifier("CID:5035")));
    }

}