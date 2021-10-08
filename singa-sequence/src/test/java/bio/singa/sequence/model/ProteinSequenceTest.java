package bio.singa.sequence.model;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class ProteinSequenceTest {

    @Test
    void shouldCreateProteinSequence() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1c0a")
                .parse();
        ProteinSequence proteinSequence = ProteinSequence.of(structure.getFirstChain());
        assertTrue(!proteinSequence.getSequenceAsString().isEmpty());
        assertTrue(ProteinSequence.of(structure.getFirstModel().getChain("B").get()).getSequenceAsString().isEmpty());
    }
}