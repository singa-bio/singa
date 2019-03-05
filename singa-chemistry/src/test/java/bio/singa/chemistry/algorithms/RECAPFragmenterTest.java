package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class RECAPFragmenterTest {

    @Test
    void fragment() {

        Set<MoleculeGraph> compounds = new HashSet<>();
        compounds.add(SmilesParser.parse("CC(N)C(=O)NC(C)C=O"));
        new RECAPFragmenter(compounds);
    }
}