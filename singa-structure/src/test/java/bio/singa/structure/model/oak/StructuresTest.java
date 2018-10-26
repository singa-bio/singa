package bio.singa.structure.model.oak;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class StructuresTest {

    @Test
    void calculateDistanceMatrix() {
        Chain chain = StructureParser.pdb()
                .pdbIdentifier("1HRR")
                .parse()
                .getFirstChain();
        final LabeledSymmetricMatrix<LeafSubstructure<?>> distanceMatrix = Structures.calculateDistanceMatrix(chain);
        assertTrue(distanceMatrix.getMainDiagonal().isZero());
        assertEquals(5.461240152199864, distanceMatrix.getElement(5, 3));
        assertEquals(3.792725405298938, distanceMatrix.getElement(17, 18));
        assertEquals(20.372810778093434, distanceMatrix.getElement(23, 11));
    }

    @Test
    void calculateAtomDistanceMatrix() {
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
    void isAlphaCarbonStructure() {
        Structure alphaCarbonStructure = StructureParser.pdb()
                .pdbIdentifier("1hrb")
                .parse();
        assertTrue(Structures.isAlphaCarbonStructure(alphaCarbonStructure));
    }

    @Test
    void isBackboneOnlyStructure() {
        Structure alphaCarbonStructure = StructureParser.pdb()
                .pdbIdentifier("2plp")
                .parse();
        assertTrue(Structures.isBackboneStructure(alphaCarbonStructure));
    }

}