package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RECAPFragmenterTest {

    @Test()
    void fragment() {
        MoleculeGraph molecule = SmilesParser.parse("CCCCN(C(=O)N(C)C(=[OH]C1CC1)N(C)Cl)c2ccccc2");
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        DirectedGraph<GenericNode<MoleculeGraph>> fragments = recapFragmenter.getFragmentSpace();
        List<MoleculeGraph> uniqueList = new ArrayList<>(recapFragmenter.getUniqueFragments());
        assertEquals(100, uniqueList.size());
    }
}