package bio.singa.structure.algorithms.molecules;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeGraph;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import bio.singa.structure.model.oak.OakLeafSubstructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoleculeRingFinderTest {

    @Test
    void findRingsInMolecule() {
        List<LeafSubstructure<?>> targetLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .parse().getAllLeafSubstructures();
        MoleculeGraph moleculeGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) targetLeafSubstructure.get(0));
        List<Set<MoleculeAtom>> rings = MoleculeRingFinder.of(moleculeGraph);
        assertEquals(rings.size(), 3);
        assertEquals(rings.get(0).size(), 5);
        assertEquals(rings.get(1).size(), 5);
        assertEquals(rings.get(2).size(), 6);
    }
}