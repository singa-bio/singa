package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.branches.Chain;
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
        String pdbIdentifier = "1acj";
        Structure structure = StructureParser.online()
                .pdbIdentifier(pdbIdentifier)
                .parse();
        Chain firstChain = structure.getFirstModel().get().getFirstChain().get();
        Path structureOutputPath = this.folder.getRoot().toPath().resolve(firstChain + ".pdb");
        StructureWriter.writeBranchSubstructure(firstChain, structureOutputPath);
        Structure reparsedStructure = StructureParser.local()
                .path(structureOutputPath)
                .parse();
        assertEquals(pdbIdentifier, reparsedStructure.getPdbIdentifier());
    }

    @Test
    public void writeLeafSubstructures() throws Exception {
    }

}