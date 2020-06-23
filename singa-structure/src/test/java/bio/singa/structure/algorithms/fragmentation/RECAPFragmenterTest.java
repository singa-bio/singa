package bio.singa.structure.algorithms.fragmentation;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RECAPFragmenterTest {

    private MoleculeGraph molecule;

    @BeforeEach
    void setUp() {
        molecule = SmilesParser.parse("CCCCN(C(=O)N(C)C(=[OH]C1CC1)N(C)Cl)c2ccccc2");
    }

    @Test()
    void fragment() {
        MoleculeGraph molecule = SmilesParser.parse("CCCCN(C(=O)N(C)C(=[OH]C1CC1)N(C)Cl)c2ccccc2");
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        DirectedGraph<GenericNode<MoleculeGraph>> fragments = recapFragmenter.getFragmentSpace();
        List<MoleculeGraph> uniqueList = new ArrayList<>(recapFragmenter.getUniqueFragments());
        assertEquals(100, uniqueList.size());
    }

    @Test
    void convertToSmiles() {
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        assertEquals(100, recapFragmenter.getUniqueFragments().size());
    }
}