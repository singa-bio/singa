package de.bioforscher.singa.structure.parser.pdb.structures;


import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.structure.parser.pdb.structures.StructureParser.*;
import static de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME;
import static de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions.Setting.GET_TITLE_FROM_FILENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StructureParserTest {

    private static Structure hemoglobin;
    private static Structure cyanase;

    @BeforeClass
    public static void parseUncomplicatedStructure() {
        // "normal" structure
        hemoglobin = online()
                .pdbIdentifier("1BUW")
                .parse();
    }

    @BeforeClass
    public static void parseResiduesWithModifiedAminoAcids() {
        cyanase = online()
                .pdbIdentifier("1DW9")
                .parse();
    }

    @Test
    public void shouldParsePDBIdentifierFromHeader() {
        assertEquals("1BUW", hemoglobin.getPdbIdentifier());
    }

    @Test
    public void shouldParseOneLineTitleFromHeader() {
        assertEquals("CRYSTAL STRUCTURE OF S-NITROSO-NITROSYL HUMAN HEMOGLOBIN A", hemoglobin.getTitle());
    }

    @Test
    public void shouldParseMultiLineTitleFromHeader() {
        assertEquals("STRUCTURE OF CYANASE REVEALS THAT A NOVEL DIMERIC AND DECAMERIC ARRANGEMENT OF SUBUNITS IS REQUIRED FOR FORMATION OF THE ENZYME ACTIVE SITE", cyanase.getTitle());
    }

    @Test
    public void shouldParseModel() {
        // parse one model of multi model structure
        Structure structure = online()
                .pdbIdentifier("1PQS")
                .model(2)
                .allChains()
                .parse();
        assertEquals(1, structure.getAllModels().size());
        assertEquals(new Integer(2), structure.getFirstModel().getIdentifier());
    }

    @Test
    public void shouldParseChain() {
        // parse one chainIdentifier of multi chainIdentifier structure
        Structure structure = online()
                .pdbIdentifier("1BRR")
                .chainIdentifier("A")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals("A", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    public void shouldParseModelAndChain() {
        // parse one model of multi model structure and only a specific chainIdentifier
        Structure structure = online()
                .pdbIdentifier("2N5E")
                .model(3)
                .chainIdentifier("B")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals(new Integer(3), structure.getFirstModel().getIdentifier());
        assertEquals("B", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    public void shouldParseChainOfMultiModel() {
        // parse only a specific chainIdentifier of all models in a structure
        Structure structure = online()
                .pdbIdentifier("2N5E")
                .chainIdentifier("B")
                .parse();
    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() {
        Structure structure = online()
                .pdbIdentifier("5T3L")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseStructureWithInsertionCodes() {
        Structure structure = online()
                .pdbIdentifier("1C0A")
                .everything()
                .parse();

        List<LeafSubstructure<?>> leavesWithInsertionCode = structure.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == 620)
                .collect(Collectors.toList());

        assertEquals(2, leavesWithInsertionCode.size());

    }

    @Test
    public void shouldParseFromLocalPDB() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Resources.getResourceAsFileLocation("pdb"));
        Structure structure = local()
                .localPDB(localPdb, "1C0A")
                .parse();
    }

    @Test
    public void shouldParseFromLocalPDBWithChainList() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Resources.getResourceAsFileLocation("pdb"));
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = local()
                .localPDB(localPdb)
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getAllLeafSubstructures().size() > 0);
    }

    @Test
    public void shouldRetrievePathOfLocalPDB() throws URISyntaxException {
        LocalPDB localPdb = new LocalPDB(Resources.getResourceAsFileLocation("pdb"));
        assertTrue(localPdb.getPathForPdbIdentifier("1C0A").endsWith("pdb/data/structures/divided/pdb/c0/1c0a/pdb1c0a.ent.gz"));
    }

    @Test
    public void shouldAssignInformationFromFileName() {
        StructureParserOptions options = StructureParserOptions.withSettings(GET_TITLE_FROM_FILENAME, GET_IDENTIFIER_FROM_FILENAME);
        Structure structure = local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .everything()
                .setOptions(options)
                .parse();

        assertEquals("1GL0_HDS_intra_E-H57_E-D102_E-S195", structure.getTitle());
        assertEquals("1GL0", structure.getPdbIdentifier());
    }

    @Test
    public void shouldParseFromInputStream() {
        InputStream inputStream = Resources.getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb");
        Structure structure = local()
                .inputStream(inputStream)
                .parse();
    }

    @Test
    public void shouldParseMultipleStructures() {
        // all have the ligand SO4
        List<Structure> structures = mmtf()
                .pdbIdentifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowErrorWhenFileDoesNotExist() {
        Structure structure = online()
                .pdbIdentifier("schalalala")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseAllChainsFromLocalFile() {
        local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .allChains()
                .parse();
    }
}