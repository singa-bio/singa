package de.bioforscher.singa.structure.algorithms.gyration;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GyrationTest {

    private static Structure structure;

    @BeforeClass
    public static void setup() {
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
        LeafSubstructure alanine = AminoAcidFamily.ALANINE.getPrototype();
        Gyration gyration = Gyration.of(alanine);
        assertEquals(1.66, gyration.getRadius(), 1E-2);
    }
}