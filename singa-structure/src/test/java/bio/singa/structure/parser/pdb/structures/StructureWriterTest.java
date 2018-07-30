package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.oak.OakStructure;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author fk
 */
public class StructureWriterTest {

    @Test
    public void writeMMTFStructure() throws IOException {

        Path tempFile = Files.createTempFile("singa_", ".mmtf.gz");
        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1acj")
                .parse();

        StructureWriter.writeMMTFStructure(structure, tempFile);
    }
}