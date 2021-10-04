package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbStructure;
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

        LocalPDBRepository localPDB = new LocalPDBRepository("/tmp/pdb", SourceLocation.OFFLINE_MMTF);
        Path path = localPDB.getPathForPdbIdentifier("1acj");

        PdbStructure structure = (PdbStructure) StructureParser.pdb()
                .pdbIdentifier("1acj")
                .parse();

        StructureWriter.mmtf()
                .structure(structure)
                .writeToPath(path);

        Structure reparsedStructure = StructureParser.local()
                .localPdbRepository(localPDB)
                .pdbIdentifier("1acj")
                .parse();

        assertEquals(structure.getAllAtoms().size(), reparsedStructure.getAllAtoms().size());
        assertEquals(structure.getAllLigands().size(),reparsedStructure.getAllLigands().size());
    }
}