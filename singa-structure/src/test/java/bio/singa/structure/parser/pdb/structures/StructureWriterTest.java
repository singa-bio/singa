package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.oak.OakStructure;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author fk
 */
public class StructureWriterTest {

    @Test
    public void writeMMTFStructure() throws IOException {
        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1acj")
                .parse();

        StructureWriter.writeMMTFStructure(structure, Paths.get("/tmp"));
    }
}