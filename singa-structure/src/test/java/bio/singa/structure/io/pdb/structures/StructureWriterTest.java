package bio.singa.structure.io.pdb.structures;

import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureWriter;
import bio.singa.structure.model.general.Structures;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.io.general.StructureRepresentationOptions.Setting.RENUMBER_ATOMS_CONSECUTIVELY;
import static bio.singa.structure.io.general.StructureRepresentationOptions.Setting.RENUMBER_CHAINS_CONSECUTIVELY;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author fk
 */
class StructureWriterTest {

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

}