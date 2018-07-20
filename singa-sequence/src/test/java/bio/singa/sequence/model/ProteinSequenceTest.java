package bio.singa.sequence.model;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class ProteinSequenceTest {

    @Test
    public void shouldCreateProteinSequence() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1c0a")
                .parse();
        ProteinSequence proteinSequence = ProteinSequence.of(structure.getFirstChain());
        assertTrue(!proteinSequence.getSequenceAsString().isEmpty());
        assertTrue(ProteinSequence.of(structure.getFirstModel().getChain("B").get()).getSequenceAsString().isEmpty());
    }
}