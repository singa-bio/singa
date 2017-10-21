package chemistry.physical.branches;


import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class BranchSubstructureTest {

    private static Structure structure;

    @BeforeClass
    public static void setup() {
        structure = StructureParser.online()
                .pdbIdentifier("1HRR")
                .parse();
    }

    @Test
    public void shouldGenerateDistanceMatrixFromLeafs() {
        Chain chain = structure.getFirstModel().getFirstChain();
        LabeledSymmetricMatrix<LeafSubstructure< ?>> actual = VectorMetricProvider.EUCLIDEAN_METRIC
                .calculateDistancesPairwise(chain.getAllLeafSubstructures(), LeafSubstructure::getPosition);
        assertTrue(actual.getMainDiagonal().isZero());
        assertEquals(5.608368621087599, actual.getElement(5, 3), 0.0);
        assertEquals(7.765433778659168, actual.getElement(17, 18), 0.0);
        assertEquals(21.53673508245474, actual.getElement(23, 11), 0.0);
    }

}