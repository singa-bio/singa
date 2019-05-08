package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

class SmilesGeneratorTest {

    @Test
    void generate() {
//        MoleculeGraph moleculeGraph = SmilesParser.parse("c1ccc2cc3ccccc3cc2c1");
        MoleculeGraph moleculeGraph = SmilesParser.parse("O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]");
        SmilesGenerator.generate(moleculeGraph);
    }
}