package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class NucleotideSequenceTest {
    @Test
    public void shouldCreateNucleotideSequence() {
        Structure structure = StructureParser.mmtf()
                .pdbIdentifier("1c0a")
                .parse();
        NucleotideSequence proteinSequence = NucleotideSequence.of(structure.getFirstChain());
        assertTrue(proteinSequence.getSequenceAsString().isEmpty());
        assertTrue(!NucleotideSequence.of(structure.getFirstModel().getChain("B").get()).getSequenceAsString().isEmpty());
    }
}