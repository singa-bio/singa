package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.identifiers.PDBIdentifier;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.iterators.converters.LocalPdbToPathConverter;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class OfflineMmtfIteratorTest {

    @Test
    void shouldIterateLocalMmtfFilesWithPath() {

        StructureParser.LocalPdb localPDB = new StructureParser.LocalPdb(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_MMTF);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        OfflineMmtfIterator<String> onlineMmtfIterator = new OfflineMmtfIterator<>(sources, LocalPdbToPathConverter.get(localPDB));
        while (onlineMmtfIterator.hasNext()) {
            String nextPdbIdentifier = onlineMmtfIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            byte[] content = onlineMmtfIterator.getContent(nextPdbIdentifier);
            MmtfStructure mmtfStructure = new MmtfStructure(content, false);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlineMmtfWithChainList() {
        StructureParser.LocalPdb localPDB = new StructureParser.LocalPdb(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_MMTF);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        OfflineMmtfIterator<String> onlineMmtfIterator = OfflineMmtfIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPdbToPathConverter.get(localPDB));
        while (onlineMmtfIterator.hasNext()) {
            String nextPdbIdentifier = onlineMmtfIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(onlineMmtfIterator.hasChain());
            byte[] content = onlineMmtfIterator.getContent(nextPdbIdentifier);
            MmtfStructure mmtfStructure = new MmtfStructure(content, false);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }


}