package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
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