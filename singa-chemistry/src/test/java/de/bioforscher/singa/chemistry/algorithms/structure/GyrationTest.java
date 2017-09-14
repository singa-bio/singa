package de.bioforscher.singa.chemistry.algorithms.structure;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GyrationTest {

    private Structure structure;

    @Before
    public void setUp() {
        structure = StructureParser.online()
                .pdbIdentifier("2q6n")
                .parse();
    }

    @Test
    public void shouldCalculateGyrationOfStructuralModel() {
        Gyration gyration = Gyration.of(structure.getFirstModel());
        assertEquals(48.74, gyration.getRadius(), 1E-2);
    }

    @Test
    public void shouldCalculateGyrationOfChain() {
        Gyration gyration = Gyration.of(structure.getFirstModel().getFirstChain());
        assertEquals(22.19, gyration.getRadius(), 1E-2);
    }

    @Test
    public void shouldCalculateGyrationOfLeafSubstructure() {
        LeafSubstructure<?, ?> alanine = AminoAcidFamily.ALANINE.getPrototype();
        Gyration gyration = Gyration.of(alanine);
        assertEquals(1.66, gyration.getRadius(), 1E-2);
    }
}