package bio.singa.structure.io.pdb.structures;

import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.StructureWriter;
import bio.singa.structure.model.cif.CifLeafSubstructure;
import bio.singa.structure.model.general.AuthLeafIdentifier;
import bio.singa.structure.model.general.Structures;
import bio.singa.structure.model.interfaces.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbStructure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.structure.io.general.StructureRepresentationOptions.Setting.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fk
 */
class StructureWriterTest {
    private static final String CONECT_RECORD = "CONECT";
    private static final String LINK_RECORD = "LINK";

    @Test
    void writeCifToPdb() {
        String pdbIdentifier = "5fq6";

        System.out.println("parsing");
        Structure structure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .parse();

        System.out.println("getting chain");
        LeafSubstructure ligand = structure.getChain(1, "Q").get()
                .getAllLeafSubstructures().iterator().next();

        System.out.println("collecting leaves");
        List<LeafSubstructure> substructuresToWrite = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : structure.getAllLeafSubstructures()) {
            if (Structures.areCloserThan(ligand, leafSubstructure, 8.0)) {
                substructuresToWrite.add(leafSubstructure);
            }
        }

        System.out.println("creating pdb representation");
        String resultingString = StructureWriter.pdb()
                .substructures(substructuresToWrite)
                .pdbIdentifier(structure.getStructureIdentifier())
                .title(structure.getTitle())
                .settings(RENUMBER_ATOMS_CONSECUTIVELY, RENUMBER_CHAINS_CONSECUTIVELY)
                .writeToString();

        System.out.println("writing");
        try {
            Path path = Files.createTempFile("singa-integration-structure-writer", pdbIdentifier + ".pdb");
            Files.write(path, resultingString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            fail("unable to write structure");
        }
    }

    @Test
    void shouldPropagateConectRecordsCifToPdb() {
        String pdbIdentifier = "5oj9";

        System.out.println("parsing");
        Structure structure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();

        // assert bond info is present after CIF parsing -- these counts assert that intra- and inter-molecule bonds are set correctly
        LeafSubstructure val = structure.getLeafSubstructure(LeafIdentifier.label().structure(pdbIdentifier).model(1).chain("A").serial(2)).get();
        // 6 bonds within valine, one outgoing/inter-molecule peptide bond
        assertEquals(7, val.getBonds().size(), "Unexpected bond count in standard amino acid");
        LeafSubstructure mhs = structure.getLeafSubstructure(LeafIdentifier.label().structure(pdbIdentifier).model(1).chain("A").serial(94)).get();
        assertEquals(14, mhs.getBonds().size(), "Unexpected bond count in MHS");
        // must select using auth schema because ligands don't have label_seq_id
        LeafSubstructure hem = structure.getLeafSubstructure(LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(201).noInsertionCode()).get();
        assertEquals(52, hem.getBonds().size(), "Unexpected bond count in HEM");

        System.out.println("creating pdb representation");
        String resultingString = StructureWriter.pdb()
                .structure(structure)
                .settings(APPEND_ALL_LIGAND_CONNECTIONS)
                .writeToString();

        // assert CONECT record behavior
        // note that these are de-duplicate (i.e. no explicit back references for some of the bidirectional connections)
        System.out.println(resultingString);
        assertEquals(56, resultingString.split(CONECT_RECORD).length);
    }

    @Test
    void shouldPropagateConectRecordsPdbToPdb() {
        String pdbIdentifier = "5oj9";

        System.out.println("parsing");
        PdbStructure structure = (PdbStructure) StructureParser.pdb()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();

        // assert bond info is present after PDB parsing
        LeafSubstructure val = structure.getLeafSubstructure(LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(1).noInsertionCode()).get();
        assertEquals(7, val.getBonds().size(), "Unexpected bond count in standard amino acid");
        LeafSubstructure mhs = structure.getLeafSubstructure(LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(93).noInsertionCode()).get();
        assertEquals(14, mhs.getBonds().size(), "Unexpected bond count in non-standard MHS residue");
        LeafSubstructure hem = structure.getLeafSubstructure(LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(201).noInsertionCode()).get();
        assertEquals(52, hem.getBonds().size(), "Unexpected bond count in HEM ligand");

        System.out.println("creating pdb representation");
        String resultingString = StructureWriter.pdb()
                .structure(structure)
                .settings(APPEND_ALL_LIGAND_CONNECTIONS)
                .writeToString();

        // assert CONECT record behavior: bonds for all inter molecule and all non-standard compounds written
        // note that these are de-duplicate (i.e. no explicit back references for some of the bidirectional connections)
        // additionally, the implicit renumbering shifts the content of CONECT records around (without losing information)
        System.out.println(resultingString);
        assertEquals(56, resultingString.split(CONECT_RECORD).length);
    }

    @Test
    void shouldRenumberPdbContent() {
        String pdbIdentifier = "5oj9";

        System.out.println("parsing");
        PdbStructure structure = (PdbStructure) StructureParser.pdb()
                .pdbIdentifier(pdbIdentifier)
                .parse();

        System.out.println("creating renumbered pdb representation");
        String resultingString = StructureWriter.pdb()
                .structure(structure)
                .settings(RENUMBER_ATOMS_CONSECUTIVELY)
                .writeToString();

        // assert CONECT record behavior: bonds for all inter molecule and all non-standard compounds written
        // note that these are de-duplicate (i.e. no explicit back references for some of the bidirectional connections)
        // additionally, the explicit renumbering shifts the content of CONECT records around (without losing information)
        assertEquals(55, resultingString.split(CONECT_RECORD).length);
    }

    @Test
    void shouldHonorMetalCoordination() {
        String pdbIdentifier = "5qrf";

        System.out.println("parsing");
        Structure pdbStructure = StructureParser.pdb()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();
        assertEquals(21, pdbStructure.getLinkEntries().size());

        System.out.println("creating pdb representation");
        String pdbResultingString = StructureWriter.pdb()
                .structure(pdbStructure)
                // there are altlocs, renumbered atoms make debugging easier
                .settings(APPEND_ALL_LIGAND_CONNECTIONS, RENUMBER_ATOMS_CONSECUTIVELY)
                .writeToString();

        assertNonpolyWaterOrder(pdbResultingString);
        assertEquals(27, pdbResultingString.split(CONECT_RECORD).length);
        assertEquals(22, pdbResultingString.split(LINK_RECORD).length);

        System.out.println("parsing");
        Structure cifStructure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();
        assertEquals(21, cifStructure.getLinkEntries().size());

        System.out.println("creating renumbered pdb representation");
        String cifResultingString = StructureWriter.pdb()
                .structure(cifStructure)
                .settings(APPEND_ALL_LIGAND_CONNECTIONS, RENUMBER_ATOMS_CONSECUTIVELY)
                .writeToString();

        assertNonpolyWaterOrder(cifResultingString);
        assertEquals(27, cifResultingString.split(CONECT_RECORD).length);
        assertEquals(22, cifResultingString.split(LINK_RECORD).length);
    }

    private void assertNonpolyWaterOrder(String content) {
        int atomId = 0;
        boolean waterLast = false;
        for (String line : content.split("\n")) {
            if (!line.startsWith("ATOM") && !line.startsWith("HETATM")) continue;

            int id = Integer.parseInt(line.substring(6, 11).trim());
            if (id > atomId) {
                atomId = id;
            } else {
                fail("atom " + id + " followed on " + atomId + " -- atom identifiers are expected to strictly monotonically increased with each line");
            }

            String compId = line.substring(17, 20).trim();
            waterLast = compId.equals("HOH");
        }

        assertTrue(waterLast, "water is expected to appear last in the outputted file");
    }

    @Test
    void shouldRenumberAllConectRecords() throws IOException {
        String pdbIdentifier = "8a10";

        System.out.println("parsing original CIF");
        Structure structure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();

        System.out.println("assert that CIF parsing doesn't label water as polymeric");
        structure.getAllLeafSubstructures()
                .stream()
                .filter(l -> l.getThreeLetterCode().equals("HOH"))
                .map(CifLeafSubstructure.class::cast)
                .forEach(l -> assertFalse(l.isPartOfPolymer(), "water '" + l.getLabelIdentifier() + "' is annotated as polymer"));

        System.out.println("convert to mmCIF with 5-character ligand name to valid PDB by setting its ligand name to LIG, retain information in a dedicated REMARK 950 entry");
        Path tmpPath = Files.createTempFile("singa-integration-structure-writer", pdbIdentifier + ".pdb");
        List<String> pdbContent = Arrays.stream(StructureWriter.pdb()
                .structure(structure)
                .settings(APPEND_ALL_LIGAND_CONNECTIONS, APPEND_RENAMED_LIGANDS)
                .writeToString()
                .split("\n"))
                .collect(Collectors.toList());
        long originalConectCount = pdbContent.stream().filter(l -> l.startsWith(CONECT_RECORD)).count();
        assertTrue(pdbContent.stream().filter(l -> !l.startsWith("REMARK")).noneMatch(l -> l.contains("A1IYK")), "5-character ligands should be renamed (outside of REMARK records)");
        assertTrue(pdbContent.stream().anyMatch(l -> l.contains("A1IYK renamed to LIG")), "original ligand name was lost");
        assertEquals(24, originalConectCount, "mismatch in number of CONECT records");
        Files.write(tmpPath, pdbContent.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());

        System.out.println("read converted PDB file again, check that bonds and original ligand names are parsed correctly");
        Structure reread = StructureParser.local()
                .path(tmpPath)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS, StructureParserOptions.Setting.ENFORCE_ORIGINAL_LIGAND_NAMES)
                .parse();
        AuthLeafIdentifier ligandIdentifier = LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(301).noInsertionCode();

        System.out.println("extract a 10 A environment around the ligand");
        pruneEverything(reread, ligandIdentifier);

        System.out.println("renumber and check that ligand connections are retained");
        List<String> renumberedContent = Arrays.stream(StructureWriter.pdb()
                        .structure(reread)
                        .settings(RENUMBER_ATOMS_CONSECUTIVELY, RENUMBER_CHAINS_CONSECUTIVELY, APPEND_REMARK_80, APPEND_ALL_LIGAND_CONNECTIONS, APPEND_RENAMED_LIGANDS)
                        .writeToString()
                        .split("\n"))
                .collect(Collectors.toList());
        long renumberedConectCount = renumberedContent.stream().filter(l -> l.startsWith(CONECT_RECORD)).count();
        assertEquals(originalConectCount, renumberedConectCount, "mismatch in number of CONECT records");
        assertEquals("A1IYK", reread.getLeafSubstructure(ligandIdentifier).get().getThreeLetterCode(), "original ligand name wasn't retained");
    }

    public void pruneEverything(Structure structure, LeafIdentifier leafIdentifier) {
        Optional<? extends LeafSubstructure> optionalLigand = structure.getLeafSubstructure(leafIdentifier);
        if (!optionalLigand.isPresent()) {
            throw new IllegalStateException("The structure does not contain leaf " + leafIdentifier);
        }

        LeafSubstructure targetLigand = optionalLigand.get();
        Collection<? extends LeafSubstructure> availableLigands = structure.getAllLeafSubstructures();
        for (LeafSubstructure ligand : availableLigands) {
            if (Structures.getClosestDistance(ligand, targetLigand) <= 10.0) {
                continue;
            }
            structure.removeLeafSubstructure(ligand);
        }
    }

    @Test
    void shouldWriteConectRecordsWithSubstructureRoute() {
        String pdbIdentifier = "8a10";
        int expectedRecordCount = 24;

        System.out.println("parsing original CIF");
        Structure structure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .parse();

        System.out.println("selecting ligand substructure");
        LeafSubstructure ligand = structure.getLeafSubstructure(LeafIdentifier.auth().structure(pdbIdentifier).model(1).chain("A").serial(301).noInsertionCode()).get();

        System.out.println("writing ligand substructure");
        List<String> pdbContent = Arrays.stream(StructureWriter.pdb()
                        .substructure(ligand)
                        .settings(APPEND_ALL_LIGAND_CONNECTIONS)
                        .writeToString()
                        .split("\n"))
                .collect(Collectors.toList());
        long conectCount = pdbContent.stream().filter(l -> l.startsWith(CONECT_RECORD)).count();
        assertEquals(expectedRecordCount, conectCount, "mismatch in number of CONECT records");

        System.out.println("writing ligand substructure");
        List<String> renumberedPdbContent = Arrays.stream(StructureWriter.pdb()
                        .substructure(ligand)
                        .settings(APPEND_ALL_LIGAND_CONNECTIONS, RENUMBER_ATOMS_CONSECUTIVELY, RENUMBER_CHAINS_CONSECUTIVELY)
                        .writeToString()
                        .split("\n"))
                .collect(Collectors.toList());
        long renumberedConectCount = renumberedPdbContent.stream().filter(l -> l.startsWith(CONECT_RECORD)).count();
        assertEquals(expectedRecordCount, renumberedConectCount, "number of CONECT records should be unchanged even if renumbered");
    }

    @Test
    void shouldWriteValidPdbFileWithLongIdentifiers() {
        String pdbIdentifier = "5T1S";

        System.out.println("parsing original CIF");
        Structure structure = StructureParser.cif()
                .pdbIdentifier(pdbIdentifier)
                .parse();

        System.out.println("writing structure (with implicit renumbering)");
        List<String> implicitlyRenumbered = Arrays.stream(StructureWriter.pdb()
                        .structure(structure)
                        .settings() // should auto-detect that residue-renumbering is required
                        .writeToString()
                        .split("\n"))
                .collect(Collectors.toList());
        for (String line : implicitlyRenumbered) {
            assertTrue(line.length() <= 80, "malformed PDB line with length " + line.length() + ": " + line);
        }
        assertTrue(implicitlyRenumbered.stream().anyMatch(line -> line.startsWith("REMARK 951")), "expected dedicated REMARK 951 record that tracks residue renumbering");

        System.out.println("writing structure (by requesting renumbering)");
        List<String> explicitlyRenumbered = Arrays.stream(StructureWriter.pdb()
                        .structure(structure)
                        .settings(RENUMBER_SUBSTRUCTURES, RENUMBER_ATOMS_CONSECUTIVELY, RENUMBER_CHAINS_CONSECUTIVELY) // shortcut that renumbers even without provided residue mapping
                        .writeToString()
                        .split("\n"))
                .collect(Collectors.toList());
        for (String line : explicitlyRenumbered) {
            assertTrue(line.length() <= 80, "malformed PDB line with length " + line.length() + ": " + line);
        }
        assertFalse(explicitlyRenumbered.stream().anyMatch(line -> line.startsWith("REMARK 951")), "no dedicated REMARK 951 record to be written when renumbering everything");
    }
}