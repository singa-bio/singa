package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakStructure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class StructureWriterTest {

    @Test
    void writeMMTFStructure() throws IOException {

        LocalPdbRepository localPDB = new LocalPdbRepository("/tmp/pdb", SourceLocation.OFFLINE_MMTF);
        Path path = localPDB.getPathForPdbIdentifier("1acj");

        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1acj")
                .parse();
        StructureWriter.writeMMTFStructure(structure, path);

        Structure reparsedStructure = StructureParser.local()
                .localPdb(localPDB)
                .pdbIdentifier("1acj")
                .parse();

        assertEquals(structure.getAllAtoms().size(), reparsedStructure.getAllAtoms().size());
        assertEquals(structure.getAllLigands().size(),reparsedStructure.getAllLigands().size());
    }
}