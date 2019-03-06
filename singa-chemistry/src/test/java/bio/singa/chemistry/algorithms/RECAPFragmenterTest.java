package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class RECAPFragmenterTest {

    @Test
    void fragment() {

        Set<MoleculeGraph> compounds = new HashSet<>();
        compounds.add(SmilesParser.parse("CC1=NN=C(S1)SCC2=C(N3C(C(C3=O)NC(=O)CN4C=NN=N4)SC2)C(=O)O"));
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(compounds);
        DirectedGraph<GenericNode<MoleculeGraph>> fragments = recapFragmenter.getFragmentGraph();
    }
}