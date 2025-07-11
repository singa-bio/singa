package bio.singa.structure.io.sifts;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.structure.model.general.AuthLeafIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cl
 */
class ResidueMapParserTest {

    @Test
    void shouldParseMappingWithInsertionCode() {
        final Map<UniProtIdentifier, Map<AuthLeafIdentifier, Integer>> map = UniProtResidueMapParser.parse("1m9u");
        final AuthLeafIdentifier leafIdentifier = new AuthLeafIdentifier("1m9u", 1, "A", 98, 'A');
        UniProtIdentifier uniprotIdentifier = new UniProtIdentifier("Q8MX72");
        assertNotNull(map.get(uniprotIdentifier).get(leafIdentifier));
    }

}