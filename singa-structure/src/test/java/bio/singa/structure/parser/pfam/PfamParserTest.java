package bio.singa.structure.parser.pfam;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fk
 */
class PfamParserTest {

    @Test
    void failWithInvalidPfamIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> PfamParser.create()
                        .version(PfamParser.PfamVersion.V31)
                        .pfamIdentifier("PF123")
                        .all()
                        .chains());
    }

    @Test
    void shouldParseChains() {
        List<Chain> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF17480")
                .all()
                .chains();
        assertEquals(2, chains.size());
    }

    @Test
    void shouldParseChainsWithChainList() {
        List<Chain> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00089")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00089.txt")))
                .chains();
        assertEquals(20, chains.size());
    }

    @Test
    void shouldParseDomains() {
        List<List<LeafSubstructure<?>>> domains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF17480")
                .all()
                .domains();
        assertEquals(2, domains.size());
    }

    @Test
    void shouldParseDomainsWithChainList() {
        List<List<LeafSubstructure<?>>> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00089")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00089.txt")))
                .domains();
        assertEquals(17, chains.size());
    }

    @Test
    void shouldParseRecurringDomains() {
        List<Chain> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00069")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00069.txt")))
                .chains();
        List<List<LeafSubstructure<?>>> domains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00069")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00069.txt")))
                .domains();
        assertEquals(2, chains.size());
        assertEquals(2, domains.size());
    }
}