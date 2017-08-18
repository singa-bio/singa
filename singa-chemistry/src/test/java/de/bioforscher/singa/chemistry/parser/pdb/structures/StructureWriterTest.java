package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.branches.StructureRepresentation;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class StructureWriterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldWritePdbIdentifierBranchSubstructure() throws Exception {
        String pdbIdentifier = "1brr";
        Structure structure = StructureParser.online()
                .pdbIdentifier(pdbIdentifier)
                .parse();
        StructuralModel structuralModel = structure.getFirstModel().get();
        Path structureOutputPath = this.folder.getRoot().toPath().resolve(structuralModel + ".pdb");

        String modelRepresentation = StructureRepresentation.composePdbRepresentation(structuralModel);

        StructureWriter.writeBranchSubstructure(structuralModel, structureOutputPath);

        Structure reparsedStructure = StructureParser.local()
                .path(structureOutputPath)
                .parse();
        assertEquals(pdbIdentifier, reparsedStructure.getPdbIdentifier());
    }

    @Test
    public void writeLeafSubstructures() throws Exception {
    }

}