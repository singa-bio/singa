package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmilesGeneratorTest {

    @Test
    void generate() {
//        MoleculeGraph moleculeGraph = SmilesParser.parse("c1ccc2cc3ccccc3cc2c1");
        String originalSmiles = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
        MoleculeGraph moleculeGraph = SmilesParser.parse(originalSmiles);
        String generatedSmiles = SmilesGenerator.generate(moleculeGraph);
        assertEquals(originalSmiles, generatedSmiles);
    }
}