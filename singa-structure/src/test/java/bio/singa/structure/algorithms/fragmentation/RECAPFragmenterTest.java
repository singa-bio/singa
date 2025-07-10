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
    void fragment() {
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        assertEquals(98, recapFragmenter.getUniqueFragments().size());
    }

    @Test
    void convertToSmiles() {
        RECAPFragmenter recapFragmenter = new RECAPFragmenter(molecule);
        assertEquals(98, recapFragmenter.getUniqueFragments().size());
    }
}