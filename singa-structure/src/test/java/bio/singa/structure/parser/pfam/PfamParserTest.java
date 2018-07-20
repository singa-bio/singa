package bio.singa.structure.parser.pfam;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class PfamParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void failWithInvalidPfamIdentifier() {
        PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF123")
                .all()
                .chains();
    }

    @Test
    public void shouldParseChains() {
        List<Chain> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF17480")
                .all()
                .chains();
        assertEquals(2, chains.size());
    }

    @Test
    public void shouldParseChainsWithChainList() {
        List<Chain> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00089")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00089.txt")))
                .chains();
        assertEquals(20, chains.size());
    }

    @Test
    public void shouldParseDomains() {
        List<List<LeafSubstructure<?>>> domains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF17480")
                .all()
                .domains();
        assertEquals(2, domains.size());
    }

    @Test
    public void shouldParseDomainsWithChainList() {
        List<List<LeafSubstructure<?>>> chains = PfamParser.create()
                .version(PfamParser.PfamVersion.V31)
                .pfamIdentifier("PF00089")
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00089.txt")))
                .domains();
        assertEquals(17, chains.size());
    }

    @Test
    public void shouldParseRecurringDomains() {
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