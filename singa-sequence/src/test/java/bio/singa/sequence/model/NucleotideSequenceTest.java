package bio.singa.sequence.model;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class NucleotideSequenceTest {
    @Test
    void shouldCreateNucleotideSequence() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1c0a")
                .parse();
        NucleotideSequence proteinSequence = NucleotideSequence.of(structure.getFirstChain());
        assertTrue(proteinSequence.getSequenceAsString().isEmpty());
        assertTrue(!NucleotideSequence.of(structure.getFirstModel().getChain("B").get()).getSequenceAsString().isEmpty());
    }
}