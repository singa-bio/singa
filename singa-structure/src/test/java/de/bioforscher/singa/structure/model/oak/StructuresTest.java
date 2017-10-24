package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class StructuresTest {

    @Test
    public void calculateDistanceMatrix() throws Exception {
        Chain chain = StructureParser.online()
                .pdbIdentifier("1HRR")
                .parse()
                .getFirstChain();
        final LabeledSymmetricMatrix<LeafSubstructure<?>> distanceMatrix = Structures.calculateDistanceMatrix(chain);
        assertTrue(distanceMatrix.getMainDiagonal().isZero());
        assertEquals(5.608368621087599, distanceMatrix.getElement(5, 3), 0.0);
        assertEquals(7.765433778659168, distanceMatrix.getElement(17, 18), 0.0);
        assertEquals(21.53673508245474, distanceMatrix.getElement(23, 11), 0.0);
    }

    @Test
    public void IsAlphaCarbonStructure() throws Exception {
        Structure alphaCarbonStructure = StructureParser.online()
                .pdbIdentifier("1zlg")
                .parse();
        assertTrue(Structures.isAlphaCarbonStructure(alphaCarbonStructure));
    }

    @Test
    public void IsBackboneOnlyStructure() {
        Structure alphaCarbonStructure = StructureParser.online()
                .pdbIdentifier("2plp")
                .parse();
        assertTrue(Structures.isBackboneStructure(alphaCarbonStructure));
    }

}