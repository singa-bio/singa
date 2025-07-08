package bio.singa.structure.algorithms.fragmentation;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.chemistry.model.MoleculeGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RECAPFragmenterTest {

    private MoleculeGraph molecule;

    @BeforeEach
    void setUp() {
        molecule = SmilesParser.parse("CCCCN(C(=O)N(C)C(=[OH]C1CC1)N(C)Cl)c2ccccc2");
    }

    @Test()
    @Disabled("unclear why results diverge")
    void fragment() {
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        assertEquals(100, recapFragmenter.getUniqueFragments().size());
    }

    @Test
    @Disabled("unclear why results diverge")
    void convertToSmiles() {
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        assertEquals(100, recapFragmenter.getUniqueFragments().size());
    }
}