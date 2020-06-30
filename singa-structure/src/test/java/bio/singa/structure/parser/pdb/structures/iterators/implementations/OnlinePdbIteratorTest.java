package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.identifiers.PDBIdentifier;
import bio.singa.structure.parser.pdb.structures.tokens.HeaderToken;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class OnlinePdbIteratorTest {

    @Test
    void shouldIterateOnlinePdbFiles() {
        List<String> sources = new ArrayList<>();
        sources.add("1uwh");
        sources.add("1bi7");
        sources.add("2src");
        sources.add("1xp0");

        OnlinePdbIterator onlinePdbIterator = new OnlinePdbIterator(sources);
        while (onlinePdbIterator.hasNext()) {
            String nextPdbIdentifier = onlinePdbIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            List<String> content = onlinePdbIterator.getContent(nextPdbIdentifier);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }


    @Test
    void shouldIterateOnlinePdbWithChainList() {
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        OnlinePdbIterator pdbIterator = new OnlinePdbIterator(Paths.get(resourceAsFileLocation), ":");
        while (pdbIterator.hasNext()) {
            String nextPdbIdentifier = pdbIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            List<String> content = pdbIterator.getContent(nextPdbIdentifier);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }



}