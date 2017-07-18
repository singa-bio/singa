package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import org.junit.BeforeClass;
import org.junit.Test;

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
        Chain chain = structure.getFirstModel().get().getFirstChain().get();
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> actual = VectorMetricProvider.EUCLIDEAN_METRIC
                .calculateDistancesPairwise(chain.getLeafSubstructures(), LeafSubstructure::getPosition);
        assertTrue(actual.getMainDiagonal().isZero());
        assertEquals(5.608368621087599, actual.getElement(5, 3), 0.0);
        assertEquals(7.765433778659168, actual.getElement(17, 18), 0.0);
        assertEquals(21.53673508245474, actual.getElement(23, 11), 0.0);
    }

}