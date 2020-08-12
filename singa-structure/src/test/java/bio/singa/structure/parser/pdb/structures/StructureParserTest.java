package bio.singa.structure.parser.pdb.structures;


import bio.singa.core.utility.Resources;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.ligands.LigandParserService;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.parser.pdb.structures.StructureParserOptions.Setting.*;
import static org.junit.jupiter.api.Assertions.*;

class StructureParserTest {

    private static Structure hemoglobin;
    private static Structure cyanase;
    private static LocalPdbRepository localPdb;
    private static LocalCifRepository localCif;
    private static LocalPdbRepository localMmtf;

    @BeforeAll
    static void parseUncomplicatedStructure() {
        // "normal" structure
        hemoglobin = StructureParser.pdb()
                .pdbIdentifier("1BUW")
                .parse();
        // more complicated
        cyanase = StructureParser.pdb()
                .pdbIdentifier("1DW9")
                .parse();

        localPdb = new LocalPdbRepository(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_PDB);
        localMmtf = new LocalPdbRepository(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_MMTF);
        localCif = new LocalCifRepository(Resources.getResourceAsFileLocation("pdbechem"));
    }


    @Test
    void shouldParsePDBIdentifierFromHeader() {
        assertEquals("1buw", hemoglobin.getPdbIdentifier());
    }

    @Test
    void shouldParseOneLineTitleFromHeader() {
        assertEquals("CRYSTAL STRUCTURE OF S-NITROSO-NITROSYL HUMAN HEMOGLOBIN A", hemoglobin.getTitle());
    }

    @Test
    void shouldParseMultiLineTitleFromHeader() {
        assertEquals("STRUCTURE OF CYANASE REVEALS THAT A NOVEL DIMERIC AND DECAMERIC ARRANGEMENT OF SUBUNITS IS REQUIRED FOR FORMATION OF THE ENZYME ACTIVE SITE", cyanase.getTitle());
    }

    @Test
    void shouldParseModel() {
        // parse one model of multi model structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1PQS")
                .model(2)
                .allChains()
                .parse();
        assertEquals(1, structure.getAllModels().size());
        assertEquals(Integer.valueOf(2), structure.getFirstModel().getModelIdentifier());
    }

    @Test
    void shouldParseChain() {
        // parse one chainIdentifier of multi chainIdentifier structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1BRR")
                .chainIdentifier("A")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals("A", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    void shouldParseModelAndChain() {
        // parse one model of multi model structure and only a specific chainIdentifier
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2N5E")
                .model(3)
                .chainIdentifier("B")
                .parse();
        assertEquals(1, structure.getAllChains().size());
        assertEquals(Integer.valueOf(3), structure.getFirstModel().getModelIdentifier());
        assertEquals("B", structure.getFirstChain().getChainIdentifier());
    }

    @Test
    void shouldParseChainOfMultiModel() {
        // parse only a specific chainIdentifier of all models in a structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2N5E")
                .chainIdentifier("B")
                .parse();
    }

    // structure with dna or rna
    @Test
    void shouldParseStructureWithNucleotides() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("5T3L")
                .everything()
                .parse();
    }

    @Test
    void shouldParseStructureWithInsertionCodes() {
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
    void shouldParseStructureWithLowerCaseInsertionCodes() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("6bb4")
                .everything()
                .parse();
        LeafIdentifier leafIdentifier = LeafIdentifier.fromString("6bb4-1-I-82c");
        List<LeafSubstructure<?>> leavesWithInsertionCode = structure.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().equals(leafIdentifier))
                .collect(Collectors.toList());
        assertEquals(1, leavesWithInsertionCode.size());
    }

    @Test
    void shouldParseFromLocalPDB() {
        Structure structure = StructureParser.local()
                .localPdb(localPdb)
                .pdbIdentifier("1C0A")
                .parse();
        assertNotNull(structure);
    }

    @Test
    void shouldParseFromLocalMMTF() {
        Structure structure = StructureParser.local()
                .localPdb(localMmtf)
                .pdbIdentifier("1C0A")
                .parse();
        assertNotNull(structure);
    }

    @Test
    void shouldParseMMTFChain() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("4v5d")
                .allModels()
                .chainIdentifier("BA")
                .parse();
        List<LeafSubstructure<?>> allLeafSubstructures = structure.getAllLeafSubstructures();
        assertEquals(3229, allLeafSubstructures.size());
    }

    @Test
    void shouldParseMMTFModel() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1pqs")
                .model(3)
                .parse();
        List<LeafSubstructure<?>> allLeafSubstructures = structure.getAllLeafSubstructures();
        assertEquals(77, allLeafSubstructures.size());
    }

    @Test
    void shouldParseFromLocalPDBWithChainList() {
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = StructureParser.local()
                .localPdb(localPdb)
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getNumberOfLeafSubstructures() > 0);
    }

    @Test
    void shouldParseFromPDBWithChainList() {
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = StructureParser.pdb()
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getNumberOfLeafSubstructures() > 0);
    }

    @Test
    void shouldRetrievePathOfLocalPDB() {
        assertTrue(localPdb.getPathForPdbIdentifier("1C0A").endsWith("pdb/data/structures/divided/pdb/c0/pdb1c0a.ent.gz"));
    }

    @Test
    void shouldAssignInformationFromFileName() {
        Structure structure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .settings(GET_TITLE_FROM_FILENAME, GET_IDENTIFIER_FROM_FILENAME)
                .everything()
                .parse();

        assertEquals("1GL0_HDS_intra_E-H57_E-D102_E-S195", structure.getTitle());
        assertEquals("1gl0", structure.getPdbIdentifier());
    }

    @Test
    void shouldIgnoreHeteroAtoms() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("3fjz")
                .settings(OMIT_HETERO_ATOMS)
                .chainIdentifier("A")
                .parse();
        assertFalse(structure.getAllLeafSubstructures().stream()
                .anyMatch(LeafSubstructure::isAnnotatedAsHeteroAtom));

        structure = StructureParser.pdb()
                .pdbIdentifier("3fjz")
                .settings(GET_HETERO_ATOMS)
                .chainIdentifier("A")
                .parse();
        assertTrue(structure.getAllLeafSubstructures().stream()
                .anyMatch(LeafSubstructure::isAnnotatedAsHeteroAtom));
    }

    @Test
    void shouldParseFromInputStream() {
        InputStream inputStream = Resources.getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb");
        Structure structure = StructureParser.local()
                .inputStream(inputStream)
                .parse();
    }

    @Test
    void shouldParseMultipleStructures() {
        // all have the ligand SO4
        List<Structure> structures = StructureParser.mmtf()
                .pdbIdentifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
    }

    @Test
    void shouldThrowErrorWhenFileDoesNotExist() {
        assertThrows(UncheckedIOException.class,
                () -> StructureParser.pdb()
                        .pdbIdentifier("invalid pdbid")
                        .everything()
                        .parse());
    }

    @Test
    void shouldParseAllChainsFromLocalFile() {
        StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .allChains()
                .parse();
    }


    @Test
    void shouldParseWithLocalCif() {
        Structure structure = StructureParser.local()
                .localCifRepository(localCif)
                .path(localPdb.getPathForPdbIdentifier("1c0a"))
                .settings(DISREGARD_CONNECTIONS)
                .parse();

        System.out.println(structure);

    }

    @Test
    void shouldParsallPdbFiles() {
        // FIXMEuse correct path in production

//        StructureParser.MultiParser multiParser = StructureParser.local()
//                .localPDB(localPDB)
//                .all();
//        int numberOfQueuedStructures = multiParser.getNumberOfQueuedStructures();
//        Structure next = multiParser.next();
    }

    @Test
    void shouldParseMultilineInChi() {
        // ;
        LeafSkeleton fad = LigandParserService.parseLeafSkeleton("FAD");
        System.out.println(fad.getInchi());
        // "
        LeafSkeleton lop = LigandParserService.parseLeafSkeleton("LOP");
        System.out.println(lop.getInchi());
        // trailing ;
        LeafSkeleton mnh = LigandParserService.parseLeafSkeleton("MNH");
        // InChI=1S/C34H34N4O4.Mn/c1-7-21-17(3)25-13-26-19(5)23(9-11-33(39)40)31(37-26)16-32-24(10-12-34(41)42)20(6)28(38-32)15-30-22(8-2)18(4)27(36-30)14-29(21)35-25;/h7-8,13-16H,1-2,9-12H2,3-6H3,(H4,35,36,37,38,39,40,41,42);/q;+6/p-2/b25-13-,26-13-,27-14-,28-15-,29-14-,30-15-,31-16-,32-16-;
        System.out.println(mnh.getInchi());
    }

}