package bio.singa.structure.parser.pdb.structures;


import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StructureParserTest {

    private static Structure hemoglobin;
    private static Structure cyanase;

    @BeforeClass
    public static void parseUncomplicatedStructure() {
        // "normal" structure
        hemoglobin = StructureParser.pdb()
                .pdbIdentifier("1BUW")
                .parse();
    }

    @BeforeClass
    public static void parseResiduesWithModifiedAminoAcids() {
        cyanase = StructureParser.pdb()
                .pdbIdentifier("1DW9")
                .parse();
    }

    @Test
    public void shouldParsePDBIdentifierFromHeader() {
        assertEquals("1buw", hemoglobin.getPdbIdentifier());
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
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1PQS")
                .model(2)
                .allChains()
                .parse();
        assertEquals(1, structure.getAllModels().size());
        assertEquals(new Integer(2), structure.getFirstModel().getModelIdentifier());
    }

    @Test
    public void shouldParseChain() {
        // parse one chainIdentifier of multi chainIdentifier structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1BRR")
                .chainIdentifier("A")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals("A", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    public void shouldParseModelAndChain() {
        // parse one model of multi model structure and only a specific chainIdentifier
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2N5E")
                .model(3)
                .chainIdentifier("B")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals(new Integer(3), structure.getFirstModel().getModelIdentifier());
        assertEquals("B", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    public void shouldParseChainOfMultiModel() {
        // parse only a specific chainIdentifier of all models in a structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2N5E")
                .chainIdentifier("B")
                .parse();
    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("5T3L")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseStructureWithInsertionCodes() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .everything()
                .parse();

        List<LeafSubstructure<?>> leavesWithInsertionCode = structure.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == 620)
                .collect(Collectors.toList());

        assertEquals(2, leavesWithInsertionCode.size());

    }

    @Test
    public void shouldParseFromLocalPDB() {
        StructureParser.LocalPDB localPdb = new StructureParser.LocalPDB(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_PDB);
        Structure structure = StructureParser.local()
                .localPDB(localPdb, "1C0A")
                .parse();
    }

    @Test
    public void shouldParseFromLocalMMTF() {
        StructureParser.LocalPDB localPdb = new StructureParser.LocalPDB(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_MMTF);
        Structure structure = StructureParser.local()
                .localPDB(localPdb, "1C0A")
                .parse();
    }

    @Test
    public void shouldParseMMTFChain() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("4v5d")
                .allModels()
                .chainIdentifier("BA")
                .parse();
        List<LeafSubstructure<?>> allLeafSubstructures = structure.getAllLeafSubstructures();
        assertEquals(3229, allLeafSubstructures.size());
    }

    @Test
    public void shouldParseMMTFModel() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1pqs")
                .model(3)
                .parse();
        List<LeafSubstructure<?>> allLeafSubstructures = structure.getAllLeafSubstructures();
        assertEquals(77, allLeafSubstructures.size());
    }

    @Test
    public void shouldParseFromLocalPDBWithChainList() {
        StructureParser.LocalPDB localPdb = new StructureParser.LocalPDB(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_PDB);
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = StructureParser.local()
                .localPDB(localPdb)
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getNumberOfLeafSubstructures() > 0);
    }

    @Test
    public void shouldRetrievePathOfLocalPDB() {
        StructureParser.LocalPDB localPdb = new StructureParser.LocalPDB(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_PDB);
        assertTrue(localPdb.getPathForPdbIdentifier("1C0A").endsWith("pdb/data/structures/divided/pdb/c0/pdb1c0a.ent.gz"));
    }

    @Test
    public void shouldAssignInformationFromFileName() {
        StructureParserOptions options = StructureParserOptions.withSettings(StructureParserOptions.Setting.GET_TITLE_FROM_FILENAME, StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME);
        Structure structure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .everything()
                .setOptions(options)
                .parse();

        assertEquals("1GL0_HDS_intra_E-H57_E-D102_E-S195", structure.getTitle());
        assertEquals("1gl0", structure.getPdbIdentifier());
    }

    @Test
    public void shouldIgnoreHeteroAtoms() {
        StructureParserOptions options = StructureParserOptions.withSettings(StructureParserOptions.Setting.OMIT_HETERO_ATOMS);
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("3fjz")
                .chainIdentifier("A")
                .setOptions(options)
                .parse();
        assertFalse(structure.getAllLeafSubstructures().stream()
                .anyMatch(LeafSubstructure::isAnnotatedAsHeteroAtom));
        options = StructureParserOptions.withSettings(StructureParserOptions.Setting.GET_HETERO_ATOMS);
        structure = StructureParser.pdb()
                .pdbIdentifier("3fjz")
                .chainIdentifier("A")
                .setOptions(options)
                .parse();
        assertTrue(structure.getAllLeafSubstructures().stream()
                .anyMatch(LeafSubstructure::isAnnotatedAsHeteroAtom));
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
        List<Structure> structures = StructureParser.mmtf()
                .pdbIdentifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowErrorWhenFileDoesNotExist() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("schalalala")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseAllChainsFromLocalFile() {
        StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .allChains()
                .parse();
    }

}