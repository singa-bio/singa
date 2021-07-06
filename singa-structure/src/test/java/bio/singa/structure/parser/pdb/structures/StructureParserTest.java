package bio.singa.structure.parser.pdb.structures;


import bio.singa.core.utility.Resources;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Ligand;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfAminoAcid;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.model.oak.OakLigand;
import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static bio.singa.structure.model.oak.StructuralEntityFilter.AtomFilter.*;
import static bio.singa.structure.parser.pdb.structures.StructureParserOptions.Setting.*;
import static org.junit.jupiter.api.Assertions.*;

class StructureParserTest {

    private static LocalPDBRepository localPdb;
    private static LocalPDBRepository localMmtf;

    @BeforeAll
    static void parseUncomplicatedStructure() {
        localPdb = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_PDB);
        localMmtf = new LocalPDBRepository(Resources.getResourceAsFileLocation("pdb"), SourceLocation.OFFLINE_MMTF);
    }

    @Test
    @DisplayName("pdb parsing - correct pdb identifier")
    void shouldParsePDBIdentifierFromHeader() {
        Structure hemoglobin = StructureParser.pdb()
                .pdbIdentifier("1BUW")
                .parse();
        assertEquals("1buw", hemoglobin.getPdbIdentifier());
    }

    @Test
    @DisplayName("pdb parsing - correct one line title")
    void shouldParseOneLineTitleFromHeader() {
        Structure hemoglobin = StructureParser.pdb()
                .pdbIdentifier("1BUW")
                .parse();
        assertEquals("CRYSTAL STRUCTURE OF S-NITROSO-NITROSYL HUMAN HEMOGLOBIN A", hemoglobin.getTitle());
    }

    @Test
    @DisplayName("pdb parsing - correct multi line title")
    void shouldParseMultiLineTitleFromHeader() {
        Structure cyanase = StructureParser.pdb()
                .pdbIdentifier("1DW9")
                .parse();
        assertEquals("STRUCTURE OF CYANASE REVEALS THAT A NOVEL DIMERIC AND DECAMERIC ARRANGEMENT OF SUBUNITS IS REQUIRED FOR FORMATION OF THE ENZYME ACTIVE SITE", cyanase.getTitle());
    }

    @Test
    @DisplayName("pdb parsing - correct model reduction")
    void shouldParseModel() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1PQS")
                .model(2)
                .allChains()
                .parse();
        assertEquals(1, structure.getAllModels().size());
        assertEquals(2, structure.getFirstModel().getModelIdentifier().intValue());
    }

    @Test
    @DisplayName("pdb parsing - correct chain reduction")
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
    @DisplayName("pdb parsing - correct model and chain reduction")
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

    @Test()
    @DisplayName("pdb parsing - correct multi model chain reduction")
    void shouldParseChainOfMultiModel() {
        // parse only a specific chainIdentifier of all models in a structure
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2N5E")
                .chainIdentifier("B")
                .parse();
        assertEquals(10, structure.getAllModels().size());
        assertEquals(1, structure.getFirstModel().getAllChains().size());
        assertEquals("B", structure.getFirstChain().getChainIdentifier());
    }

    // structure with dna or rna
    @Test
    @DisplayName("pdb parsing - correct nucleotide assignment")
    void shouldParseStructureWithNucleotides() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("5T3L")
                .everything()
                .parse();
        assertEquals(12, structure.getFirstChain().getAllNucleotides().size());
    }

    @Test
    @DisplayName("pdb parsing - correct default insertion code handling")
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
    @DisplayName("pdb parsing - correct lower case insertion code handling")
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
    @DisplayName("pdb parsing - correct local pdb resolving")
    void shouldParseFromLocalPDB() {
        Structure structure = StructureParser.local()
                .localPdbRepository(localPdb)
                .pdbIdentifier("1C0A")
                .parse();
        assertNotNull(structure);
    }

    @Test
    @DisplayName("mmtf parsing - correct local mmtf resolving")
    void shouldParseFromLocalMMTF() {
        Structure structure = StructureParser.local()
                .localPdbRepository(localMmtf)
                .pdbIdentifier("1C0A")
                .parse();
        assertNotNull(structure);
    }

    @Test
    @DisplayName("mmtf parsing - correct chain reduction")
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
    @DisplayName("mmtf parsing - correct model reduction")
    void shouldParseMMTFModel() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1pqs")
                .model(3)
                .parse();
        List<LeafSubstructure<?>> allLeafSubstructures = structure.getAllLeafSubstructures();
        assertEquals(77, allLeafSubstructures.size());
    }

    @Test
    @DisplayName("pdb parsing - correct local chain list handling")
    void shouldParseFromLocalPDBWithChainList() {
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = StructureParser.local()
                .localPdbRepository(localPdb)
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getNumberOfLeafSubstructures() > 0);
    }

    @Test
    @DisplayName("pdb parsing - correct online chain list handling")
    void shouldParseFromPDBWithChainList() {
        Path chainList = Paths.get(Resources.getResourceAsFileLocation("chain_list.txt"));
        List<Structure> structure = StructureParser.pdb()
                .chainList(chainList, ":")
                .parse();
        assertTrue(structure.get(0).getNumberOfLeafSubstructures() > 0);
    }

    @Test
    @DisplayName("pdb parsing - correct local repository resolving handling")
    void shouldRetrievePathOfLocalPDB() {
        assertTrue(localPdb.getPathForPdbIdentifier("1C0A").endsWith("pdb/data/structures/divided/pdb/c0/pdb1c0a.ent.gz"));
    }

    @Test
    @DisplayName("pdb parsing - correct title from filename")
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
    @DisplayName("pdb parsing - ignore hetero atoms")
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
    @DisplayName("pdb parsing - correct parsing from input stream")
    void shouldParseFromInputStream() {
        InputStream inputStream = Resources.getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb");
        Structure structure = StructureParser.local()
                .inputStream(inputStream)
                .parse();
        assertNotNull(structure);
    }

    @Test
    @DisplayName("pdb parsing - correct parsing of multiple structures")
    void shouldParseMultipleMMTFStructures() {
        // all have the ligand SO4
        List<Structure> structures = StructureParser.mmtf()
                .pdbIdentifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
        assertEquals(4, structures.size());
    }

    @Test
    @DisplayName("pdb parsing - failure for invalid names")
    void shouldThrowErrorWhenFileDoesNotExist() {
        assertThrows(UncheckedIOException.class,
                () -> StructureParser.pdb()
                        .pdbIdentifier("invalid pdbid")
                        .everything()
                        .parse());
    }

    @Test
    @DisplayName("pdb parsing - correct number of chains")
    void shouldParseAllChainsFromLocalFile() {
        Structure structure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .allChains()
                .parse();
        assertEquals(1, structure.getAllChains().size());
    }

    @Test
    @DisplayName("pdb parsing - correctly omitting deuterium")
    void shouldParseStructureWithDeuterium() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2r24")
                .settings(OMIT_HYDROGENS)
                .parse();
        assertEquals(0, structure.getAllAtoms().stream()
                .filter(isHydrogen())
                .count());
    }

    @Test
    @DisplayName("pdb parsing - correctly parse inchi from REMARK and connections from CONECT records")
    void shouldParsePyMolExportPeptideConnections() {
        // we want connections but cannot guarantee unique atom names
        StructureParser.SingleParserFacade facade = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("peptide_pymol_export.pdb"))
                .settings(ENFORCE_CONNECTIONS)
                .everything();
        Structure structure = facade.parse();
        assertEquals(87, ((OakLigand) structure.getAllLigands().get(0)).getBonds().size());
        String inchi = facade.getIterator().getSkeletons().get("P_B").getInchi();
        assertEquals("InChI=1S/C54H85N15O15S/c1-30(55)53(84)69-21-12-18-42(69)52(83)68-40(28-72)50(81)61-32(3)45(76)66-39(27-71)49(80)60-31(2)44(75)64-38(24-34-15-9-6-10-16-34)48(79)65-37(23-33-13-7-5-8-14-33)46(77)59-25-43(74)63-36(19-22-85-4)47(78)67-41(29-73)51(82)62-35(26-70)17-11-20-58-54(56)57/h5-10,13-16,30-32,35-42,54,58,70-73H,11-12,17-29,55-57H2,1-4H3,(H,59,77)(H,60,80)(H,61,81)(H,62,82)(H,63,74)(H,64,75)(H,65,79)(H,66,76)(H,67,78)(H,68,83)/t30-,31-,32-,35-,36-,37-,38-,39-,40-,41-,42-/m0/s1", inchi);
    }

    @Test
    @DisplayName("pdb parsing - correctly parse resolution from REMARK")
    void shouldParseResolution() {
        // we want connections but cannot guarantee unique atom names
        Structure hemoglobin = StructureParser.pdb()
                .pdbIdentifier("1BUW")
                .parse();
        assertEquals(1.9, hemoglobin.getResolution());
    }

    @Test
    @DisplayName("pdb parsing - correctly parse mutations from SEQADV")
    void shouldParseMutations() {
        // we want connections but cannot guarantee unique atom names
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("6sl6")
                .parse();
        Optional<AminoAcid> aminoAcidOptional = structure.getAminoAcid(LeafIdentifier.fromString("6sl6-1-A-138"));
        if (aminoAcidOptional.isPresent()) {
            OakAminoAcid aminoAcid = ((OakAminoAcid) aminoAcidOptional.get());
            assertTrue(aminoAcid.isMutated());
            assertEquals(AminoAcidFamily.ALANINE, aminoAcid.getWildTypeResidue());
        } else {
            fail("could not find mutated amino acid");
        }
    }

    @Test
    @DisplayName("pdb parsing - correctly parse assemblies")
    void shouldParseAssemblies() {
        // we want connections but cannot guarantee unique atom names
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("6dm8")
                .parse();
        Map<String, List<String>> assemblies = ((OakStructure) structure).getBiologicalAssemblies();
        structure.getCopy();
        assertEquals(8, assemblies.size());
    }

}