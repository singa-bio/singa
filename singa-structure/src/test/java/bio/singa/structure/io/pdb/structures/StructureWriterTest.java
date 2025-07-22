package bio.singa.structure.io.pdb.structures;

import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.StructureWriter;
import bio.singa.structure.model.general.Structures;
import bio.singa.structure.model.interfaces.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbStructure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static bio.singa.structure.io.general.StructureRepresentationOptions.Setting.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fk
 */
class StructureWriterTest {
    private static final String CONECT_RECORD = "CONECT";

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
            Files.write(Paths.get("/tmp/" + pdbIdentifier + ".pdb"), resultingString.getBytes(StandardCharsets.UTF_8));
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
        assertEquals(55, resultingString.split(CONECT_RECORD).length);
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
}