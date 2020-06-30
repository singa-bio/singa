package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.identifiers.PDBIdentifier;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileLocationToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.LocalPdbToPathConverter;
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
class OfflinePdbIteratorTest {

    @Test
    void shouldIterateOfflinePdbFilesWithFileLocation() {

        List<String> sources = new ArrayList<>();
        sources.add(Resources.getResourceAsFileLocation("RF00167/2eeu.pdb"));
        sources.add(Resources.getResourceAsFileLocation("RF00167/3gao.pdb"));
        sources.add(Resources.getResourceAsFileLocation("RF00167/4lx5.pdb"));

        OfflinePdbIterator<String> onlinePdbIterator = new OfflinePdbIterator<>(sources, FileLocationToPathConverter.get());
        while (onlinePdbIterator.hasNext()) {
            String nextFileLocation = onlinePdbIterator.next();
            String nextPdbIdentifier = PDBIdentifier.extractFirst(nextFileLocation);
            List<String> content = onlinePdbIterator.getContent(nextFileLocation);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOfflinePdbFilesWithPath() {

        List<Path> sources = new ArrayList<>();
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/2eeu.pdb")));
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/3gao.pdb")));
        sources.add(Paths.get(Resources.getResourceAsFileLocation("RF00167/4lx5.pdb")));

        OfflinePdbIterator<Path> onlinePdbIterator = new OfflinePdbIterator<>(sources, IdentityConverter.get(Path.class));
        while (onlinePdbIterator.hasNext()) {
            Path nextFileLocation = onlinePdbIterator.next();
            String nextPdbIdentifier = PDBIdentifier.extractFirst(nextFileLocation.getFileName().toString());
            List<String> content = onlinePdbIterator.getContent(nextFileLocation);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateLocalPdbFilesWithIdentifiers() {

        StructureParser.LocalPdb localPdb = new StructureParser.LocalPdb(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        List<String> sources = new ArrayList<>();
        sources.add("1c0a");

        OfflinePdbIterator<String> onlinePdbIterator = new OfflinePdbIterator<>(sources, LocalPdbToPathConverter.get(localPdb));
        while (onlinePdbIterator.hasNext()) {
            String nextPdbIdentifier = onlinePdbIterator.next();
            List<String> content = onlinePdbIterator.getContent(nextPdbIdentifier);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }

    }

    @Test
    void shouldIterateOnlinePdbWithChainList() {
        StructureParser.LocalPdb localPdb = new StructureParser.LocalPdb(Resources.getResourceAsFileLocation("pdb/"), SourceLocation.OFFLINE_PDB);
        String resourceAsFileLocation = Resources.getResourceAsFileLocation("chain_list.txt");
        OfflinePdbIterator<String> pdbIterator = OfflinePdbIterator.fromChainList(Paths.get(resourceAsFileLocation), ":", LocalPdbToPathConverter.get(localPdb));
        while (pdbIterator.hasNext()) {
            String nextPdbIdentifier = pdbIterator.next();
            assertTrue(PDBIdentifier.PATTERN.matcher(nextPdbIdentifier).matches());
            assertTrue(pdbIterator.hasChain());
            List<String> content = pdbIterator.getContent(nextPdbIdentifier);
            String pdbIdentifier = HeaderToken.ID_CODE.extract(content.iterator().next());
            assertTrue(pdbIdentifier.equalsIgnoreCase(nextPdbIdentifier));
        }
    }




}