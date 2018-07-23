package bio.singa.structure.parser.sifts;

import bio.singa.structure.model.identifiers.LeafIdentifier;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author cl
 */
public class ResidueMapParserTest {

    @Test
    public void shouldParseMappingWithInsertionCode() {
        final Map<LeafIdentifier, Integer> map = UniProtResidueMapParser.parse("1m9u");
        final LeafIdentifier leafIdentifier = new LeafIdentifier("1m9u", 1, "A", 98, 'A');
        assertNotNull(map.get(leafIdentifier));
    }

}