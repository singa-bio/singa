package bio.singa.structure.parser.sifts;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.LeafIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cl
 */
class ResidueMapParserTest {

    @Test
    void shouldParseMappingWithInsertionCode() {
        final Map<UniProtIdentifier, Map<LeafIdentifier, Integer>> map = UniProtResidueMapParser.parse("1m9u");
        final LeafIdentifier leafIdentifier = new LeafIdentifier("1m9u", 1, "A", 98, 'A');
        UniProtIdentifier uniprotIdentifier = new UniProtIdentifier("Q8MX72");
        assertNotNull(map.get(uniprotIdentifier).get(leafIdentifier));
    }

}