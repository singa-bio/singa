package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.structure.model.interfaces.*;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class StructuresTest {

    @Test
    public void calculateDistanceMatrix() {
        Chain chain = StructureParser.pdb()
                .pdbIdentifier("1HRR")
                .parse()
                .getFirstChain();
        final LabeledSymmetricMatrix<LeafSubstructure<?>> distanceMatrix = Structures.calculateDistanceMatrix(chain);
        assertTrue(distanceMatrix.getMainDiagonal().isZero());
        assertEquals(5.461240152199864, distanceMatrix.getElement(5, 3), 0.0);
        assertEquals(3.792725405298938, distanceMatrix.getElement(17, 18), 0.0);
        assertEquals(20.372810778093434, distanceMatrix.getElement(23, 11), 0.0);
    }

    @Test
    public void calculateAtomDistanceMatrix() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("5kqr")
                .chainIdentifier("A")
                .parse();
        List<Atom> atoms = structure.getAllLeafSubstructures().stream().filter(AminoAcid.class::isInstance)
                .map(LeafSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        LabeledSymmetricMatrix<Atom> atomDistanceMatrix = Structures.calculateAtomDistanceMatrix(atoms);
        Pair<Integer> maximalElement = Matrices.getPositionsOfMaximalElement(atomDistanceMatrix).get(0);
        assertEquals(59.2191915, atomDistanceMatrix.getElement(maximalElement.getFirst(), maximalElement.getSecond()), 1E-6);
    }

    @Test
    public void isAlphaCarbonStructure() {
        Structure alphaCarbonStructure = StructureParser.pdb()
                .pdbIdentifier("1hrb")
                .parse();
        assertTrue(Structures.isAlphaCarbonStructure(alphaCarbonStructure));
    }

    @Test
    public void isBackboneOnlyStructure() {
        Structure alphaCarbonStructure = StructureParser.pdb()
                .pdbIdentifier("2plp")
                .parse();
        assertTrue(Structures.isBackboneStructure(alphaCarbonStructure));
    }

}