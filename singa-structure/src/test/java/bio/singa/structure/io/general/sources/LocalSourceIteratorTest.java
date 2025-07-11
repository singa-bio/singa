package bio.singa.structure.io.general.sources;

import bio.singa.core.utility.Resources;
import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.io.general.converters.FileLocationToPathConverter;
import bio.singa.structure.io.general.converters.IdentityConverter;
import bio.singa.structure.io.general.converters.LocalPdbToPathConverter;
import bio.singa.structure.io.general.LocalStructureRepository;
import bio.singa.structure.io.general.SourceLocation;
import bio.singa.structure.io.pdb.tokens.HeaderToken;
import org.junit.jupiter.api.Test;
import org.rcsb.cif.model.CifFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
            assertInstanceOf(List.class, content);
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
            assertInstanceOf(List.class, content);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateLocalPdbFilesWithIdentifiers() {

        LocalStructureRepository localPdb = new LocalStructureRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        LocalSourceIterator<String> onlinePdbIterator = new LocalSourceIterator<>(sources, LocalPdbToPathConverter.get(localPdb));
        while (onlinePdbIterator.hasNext()) {
            String nextPdbIdentifier = onlinePdbIterator.next();
            Object content = onlinePdbIterator.getContent(nextPdbIdentifier);
            assertInstanceOf(List.class, content);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlinePdbWithChainList() {
        LocalStructureRepository localPdb = new LocalStructureRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        LocalSourceIterator<String> iterator = LocalSourceIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPdbToPathConverter.get(localPdb));
        while (iterator.hasNext()) {
            String nextPdbIdentifier = iterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(iterator.hasChain());
            Object content = iterator.getContent(nextPdbIdentifier);
            assertInstanceOf(List.class, content);
            List<String> strings = (List<String>) content;
            String pdbIdentifier = HeaderToken.ID_CODE.extract(strings.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }

    @Test
    void shouldIterateLocalBciFilesWithPath() {

        LocalStructureRepository localPDB = new LocalStructureRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_BCIF);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        LocalSourceIterator<String> localBcifIterator = new LocalSourceIterator<>(sources, LocalPdbToPathConverter.get(localPDB));
        while (localBcifIterator.hasNext()) {
            String nextPdbIdentifier = localBcifIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            Object content = localBcifIterator.getContent(nextPdbIdentifier);
            assertInstanceOf(CifFile.class, content);
            CifFile cifFile = (CifFile) content;
            String identifier = cifFile.getBlocks().get(0).getBlockHeader();
            assertTrue(identifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlineBcifWithChainList() {
        LocalStructureRepository localPDB = new LocalStructureRepository(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_BCIF);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        LocalSourceIterator<String> onlineBcifIterator = LocalSourceIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPdbToPathConverter.get(localPDB));
        while (onlineBcifIterator.hasNext()) {
            String nextPdbIdentifier = onlineBcifIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(onlineBcifIterator.hasChain());
            Object content = onlineBcifIterator.getContent(nextPdbIdentifier);
            assertInstanceOf(CifFile.class, content);
            CifFile cifFile = (CifFile) content;
            String identifier = cifFile.getBlocks().get(0).getBlockHeader();
            assertTrue(identifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }

}