package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser.LocalPDB;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Test;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME;
import static de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions.Setting.GET_TITLE_FROM_FILENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StructureParserTest {

    @Test
    public void shouldParseUncomplicatedStructure() {
        // "normal" structure
        Structure structure = StructureParser.online()
                .pdbIdentifier("1BUW")
                .parse();
    }

    @Test
    public void shouldParseModel() {
        // parse one model of multi model structure
        Structure structure = StructureParser.online()
                .pdbIdentifier("1PQS")
                .model(2)
                .allChains()
                .parse();
        assertEquals(1, structure.getAllModels().size());
        assertEquals(new Integer(2), structure.getFirstModel().get().getIdentifier());
    }

    @Test
    public void shouldParseChain() {
        // parse one chainIdentifier of multi chainIdentifier structure
        Structure structure = StructureParser.online()
                .pdbIdentifier("1BRR")
                .chainIdentifier("A")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals("A", structure.getFirstChain().get().getIdentifier());
    }

    @Test
    public void shouldParseModelAndChain() {
        // parse one model of multi model structure and only a specific chainIdentifier
        Structure structure = StructureParser.online()
                .pdbIdentifier("2N5E")
                .model(3)
                .chainIdentifier("B")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals(new Integer(3), structure.getFirstModel().get().getIdentifier());
        assertEquals("B", structure.getFirstChain().get().getIdentifier());
    }

    @Test
    public void shouldParseChainOfMultiModel() {
        // parse only a specific chainIdentifier of all models in a structure
        Structure structure = StructureParser.online()
                .pdbIdentifier("2N5E")
                .chainIdentifier("B")
                .parse();
    }


    // structure with modified amino acids
    @Test
    public void shouldParseResiduesWithModifiedAminoAcids() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("1DW9")
                .everything()
                .parse();
    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("5T3L")
                .everything()
                .parse();
    }

    // structure with modified nucleotides
    @Test
    public void shouldParseStructureWithInsertionCodes() {
        // TODO some strange bonds between 620 and 621 issue #41
        Structure structure = StructureParser.online()
                .pdbIdentifier("1C0A")
                .everything()
                .parse();

        List<LeafSubstructure<?, ?>> leavesWithInsertionCode = structure.getAllLeaves().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == 620)
                .collect(Collectors.toList());

        assertEquals(2, leavesWithInsertionCode.size());

    }

    @Test
    public void shouldParseFromLocalPDB() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Paths.get(Thread.currentThread().getContextClassLoader().getResource("pdb").toURI()).toString());
        Structure structure = StructureParser.local()
                .localPDB(localPdb, "1C0A")
                .parse();
    }

    @Test
    public void shouldParseFromLocalPDBWithChainList() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Paths.get(Thread.currentThread().getContextClassLoader().getResource("pdb").toURI()).toString());
        Path chainList = Paths.get(Thread.currentThread().getContextClassLoader().getResource("chain_list.txt").toURI());
        List<Structure> structure = StructureParser.local()
                .localPDB(localPdb)
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getAllLeaves().size() > 0);
    }

    @Test
    public void shouldRetrievePathOfLocalPDB() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Paths.get(Thread.currentThread().getContextClassLoader().getResource("pdb").toURI()).toString());
        assertTrue(localPdb.getPathForPdbIdentifier("1C0A").endsWith("pdb/data/structures/divided/pdb/c0/1c0a/pdb1c0a.ent.gz"));
    }

    @Test
    public void shouldAssignInformationFromFileName() {
        StructureParserOptions options = StructureParserOptions.withSettings(GET_TITLE_FROM_FILENAME, GET_IDENTIFIER_FROM_FILENAME);
        Structure structure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFilepath("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .everything()
                .setOptions(options)
                .parse();

        assertEquals("1GL0_HDS_intra_E-H57_E-D102_E-S195", structure.getTitle());
        assertEquals("1gl0", structure.getPdbIdentifier());
    }

    @Test
    public void shouldParseFromInputStream() {
        InputStream inputStream = Resources.getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb");
        Structure structure = StructureParser.local()
                .inputStream(inputStream)
                .parse();
    }

    @Test
    public void shouldParseMultipleStructures() {
        // all have the ligand SO4
        List<Structure> structures = StructureParser.online()
                .pdbIdentifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowErrorWhenFileDoesNotExist() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("schalalala")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseAllChainsFromLocalFile() {
        StructureParser.local()
                .fileLocation(Resources.getResourceAsFilepath("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .allChains()
                .parse();
    }

}