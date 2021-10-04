package bio.singa.structure.model.pdb;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.general.Structures;
import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureRenumberer;
import bio.singa.structure.parser.pdb.structures.StructureSelector;
import org.junit.jupiter.api.Test;

import java.util.*;
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
        final LabeledSymmetricMatrix<LeafSubstructure> distanceMatrix = Structures.calculateDistanceMatrix(chain);
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

    @Test
    void renumberStructure() {
        PdbStructure structure = ((PdbStructure) StructureParser.pdb()
                .pdbIdentifier("1szi")
                .chainIdentifier("A")
                .parse());
        PdbLeafIdentifier leafIdentifier = new PdbLeafIdentifier("1szi", 1, "A", 206);
        Map<PdbLeafIdentifier, Integer> renumberingMap = new TreeMap<>();
        renumberingMap.put(leafIdentifier, 202);
        Structure renumberStructure = StructureRenumberer.renumberLeaveSubstructuresWithMap(structure, renumberingMap);
        StructureSelector.selectFrom(renumberStructure)
                .model(1)
                .chain("A")
                .aminoAcid(202)
                .selectAminoAcid();
    }

    @Test
    void shouldFindAtoms() {
        PdbStructure structure = ((PdbStructure) StructureParser.pdb()
                .pdbIdentifier("1szi")
                .chainIdentifier("A")
                .parse());
        Vector3D atomCoordinate = new Vector3D(51.172, 37.528, -36.507);
        Optional<Map.Entry<UniqueAtomIdentifier, Atom>> atomByCoordinate = structure.getAtomByCoordinate(atomCoordinate, 0.1);
        if (atomByCoordinate.isPresent()) {
            Atom atom = atomByCoordinate.get().getValue();
            assertEquals(5293, (int)atom.getAtomIdentifier());
        }
    }

}