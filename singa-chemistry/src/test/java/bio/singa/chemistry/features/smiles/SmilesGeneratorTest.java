package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmilesGeneratorTest {

    @Test
    void generate() throws IOException {
        String originalSmiles = "[C]([C](C(=O)[O])[N])C(=O)[N]";
        MoleculeGraph moleculeGraph = SmilesParser.parse(originalSmiles);
        String generatedSmiles = SmilesGenerator.generate(moleculeGraph);
        assertEquals(originalSmiles, generatedSmiles);
    }
}