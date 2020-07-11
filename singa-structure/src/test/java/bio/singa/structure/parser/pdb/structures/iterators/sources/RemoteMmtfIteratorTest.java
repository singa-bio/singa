package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.core.utility.Resources;
import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.model.mmtf.MmtfStructure;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class RemoteMmtfIteratorTest {

    @Test
    void shouldIterateOnlineMmtfFiles() {

        List<String> sources = new ArrayList<>();
        sources.add("1uwh");
        sources.add("1bi7");
        sources.add("2src");
        sources.add("1xp0");

        RemoteMmtfSourceIterator onlinePdbIterator = new RemoteMmtfSourceIterator(sources);
        while (onlinePdbIterator.hasNext()) {
            String nextPdbIdentifier = onlinePdbIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            byte[] content = onlinePdbIterator.getContent(nextPdbIdentifier);
            MmtfStructure mmtfStructure = new MmtfStructure(content);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlineMmtfWithChainList() {
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        RemoteMmtfSourceIterator onlineMmtfIterator = new RemoteMmtfSourceIterator(Paths.get(resourceAsFileLocation), ":");
        while (onlineMmtfIterator.hasNext()) {
            String nextPdbIdentifier = onlineMmtfIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(onlineMmtfIterator.hasChain());
            byte[] content = onlineMmtfIterator.getContent(nextPdbIdentifier);
            MmtfStructure mmtfStructure = new MmtfStructure(content);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }

}