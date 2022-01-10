package bio.singa.structure.io.pdb.structures;

import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureWriter;
import bio.singa.structure.model.interfaces.Structure;
import org.junit.jupiter.api.Test;

/**
 * @author fk
 */
class StructureWriterTest {

    @Test
    void writeCifToPdb() {
        Structure structure = StructureParser.cif()
                .pdbIdentifier("1C0A")
                .parse();

        String resultingString = StructureWriter.pdb()
                .structure(structure)
                .writeToString();

        System.out.println(resultingString);
    }
}