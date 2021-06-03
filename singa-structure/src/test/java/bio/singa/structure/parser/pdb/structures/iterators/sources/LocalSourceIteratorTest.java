package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.core.utility.Resources;
import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.LocalPDBRepository;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileLocationToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.LocalPDBToPathConverter;
import bio.singa.structure.parser.pdb.structures.tokens.HeaderToken;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class LocalSourceIteratorTest {

    @Test
    void shouldIterateOfflinePdbFilesWithFileLocation() {

        List<String> sources = new ArrayList<>();
        sources.add(Resources.getResourceAsFileLocation("RF00167/2eeu.pdb"));
        sources.add(Resources.getResourceAsFileLocation("RF00167/3gao.pdb"));
        sources.add(Resources.getResourceAsFileLocation("RF00167/4lx5.pdb"));

        LocalSourceIterator<String> onlinePdbIterator = new LocalSourceIterator<>(sources, FileLocationToPathConverter.get());
        while (onlinePdbIterator.hasNext()) {
            String nextFileLocation = onlinePdbIterator.next();
            String nextPdbIdentifier = PDBIdentifier.extractLast(nextFileLocation);
            Object content = onlinePdbIterator.getContent(nextFileLocation);
            assertTrue(content instanceof List);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOfflinePdbFilesWithPath() {

        List<Path> sources = new ArrayList<>();
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/2eeu.pdb")));
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/3gao.pdb")));
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/4lx5.pdb")));

        LocalSourceIterator<Path> onlinePdbIterator = new LocalSourceIterator<>(sources, IdentityConverter.get(Path.class));
        while (onlinePdbIterator.hasNext()) {
            Path nextFileLocation = onlinePdbIterator.next();
            String nextPdbIdentifier = PDBIdentifier.extractLast(nextFileLocation.getFileName().toString());
            Object content = onlinePdbIterator.getContent(nextFileLocation);
            assertTrue(content instanceof List);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateLocalPdbFilesWithIdentifiers() {

        LocalPDBRepository localPdb = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        LocalSourceIterator<String> onlinePdbIterator = new LocalSourceIterator<>(sources, LocalPDBToPathConverter.get(localPdb));
        while (onlinePdbIterator.hasNext()) {
            String nextPdbIdentifier = onlinePdbIterator.next();
            Object content = onlinePdbIterator.getContent(nextPdbIdentifier);
            assertTrue(content instanceof List);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlinePdbWithChainList() {
        LocalPDBRepository localPdb = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        LocalSourceIterator<String> iterator = LocalSourceIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPDBToPathConverter.get(localPdb));
        while (iterator.hasNext()) {
            String nextPdbIdentifier = iterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(iterator.hasChain());
            Object content = iterator.getContent(nextPdbIdentifier);
            assertTrue(content instanceof List);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }

    @Test
    void shouldIterateLocalMmtfFilesWithPath() {

        LocalPDBRepository localPDB = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_MMTF);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        LocalSourceIterator<String> onlineMmtfIterator = new LocalSourceIterator<>(sources, LocalPDBToPathConverter.get(localPDB));
        while (onlineMmtfIterator.hasNext()) {
            String nextPdbIdentifier = onlineMmtfIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            Object content = onlineMmtfIterator.getContent(nextPdbIdentifier);
            assertTrue(content instanceof byte[]);
            byte[] bytes = (byte[]) content;
            MmtfStructure mmtfStructure = new MmtfStructure(bytes, false);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlineMmtfWithChainList() {
        LocalPDBRepository localPDB = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_MMTF);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        LocalSourceIterator<String> onlineMmtfIterator = LocalSourceIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPDBToPathConverter.get(localPDB));
        while (onlineMmtfIterator.hasNext()) {
            String nextPdbIdentifier = onlineMmtfIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(onlineMmtfIterator.hasChain());
            Object content = onlineMmtfIterator.getContent(nextPdbIdentifier);
            assertTrue(content instanceof byte[]);
            byte[] bytes = (byte[]) content;
            MmtfStructure mmtfStructure = new MmtfStructure(bytes, false);
            String pdbIdentifier = mmtfStructure.getPdbIdentifier();
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }

}