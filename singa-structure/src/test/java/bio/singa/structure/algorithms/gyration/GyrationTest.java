package bio.singa.structure.algorithms.gyration;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GyrationTest {

    private static Structure structure;

    @BeforeAll
    static void initialize() {
        structure = StructureParser.pdb()
                .pdbIdentifier("2q6n")
                .parse();
    }

    @Test
    void shouldCalculateGyrationOfStructuralModel() {
        Gyration gyration = Gyration.of(structure.getFirstModel());
        assertEquals(48.74, gyration.getRadius(), 1E-2);
    }

    @Test
    void shouldCalculateGyrationOfChain() {
        Gyration gyration = Gyration.of(structure.getFirstModel().getFirstChain());
        assertEquals(22.19, gyration.getRadius(), 1E-2);
    }

}