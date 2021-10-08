package bio.singa.structure.io.pdb.structures;

import bio.singa.structure.io.general.LocalStructureRepository;
import bio.singa.structure.io.general.SourceLocation;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureWriter;
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

        LocalStructureRepository localPDB = new LocalStructureRepository("/tmp/pdb", SourceLocation.OFFLINE_MMTF);
        Path path = localPDB.getPathForPdbIdentifier("1acj");

        PdbStructure structure = (PdbStructure) StructureParser.pdb()
                .pdbIdentifier("1acj")
                .parse();

        StructureWriter.mmtf()
                .structure(structure)
                .writeToPath(path);

        Structure reparsedStructure = StructureParser.local()
                .localStructureRepository(localPDB)
                .pdbIdentifier("1acj")
                .parse();

        assertEquals(structure.getAllAtoms().size(), reparsedStructure.getAllAtoms().size());
        assertEquals(structure.getAllLigands().size(),reparsedStructure.getAllLigands().size());
    }
}