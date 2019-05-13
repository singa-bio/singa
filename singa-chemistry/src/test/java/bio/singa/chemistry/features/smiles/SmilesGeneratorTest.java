package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmilesGeneratorTest {

    @Test
    void generate() {
//        String originalSmiles = "CCC(C)C(C(=O)O)NC(=O)C1CCCN1";
//        String originalSmiles = "C1=CC=C2C(=C1)C(=CN2)CC(C(=O)O)N";
        String originalSmiles = "C(C(C(=O)O)N)C(=O)N";
        MoleculeGraph moleculeGraph = SmilesParser.parse(originalSmiles);
//        String originalSmiles = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
//        MoleculeGraph moleculeGraph = SmilesParser.parse(originalSmiles);
        String generatedSmiles = SmilesGenerator.generate(moleculeGraph);
        assertEquals(originalSmiles, generatedSmiles);
    }
}