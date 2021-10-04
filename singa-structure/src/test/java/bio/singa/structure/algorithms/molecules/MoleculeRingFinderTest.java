package bio.singa.structure.algorithms.molecules;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoleculeRingFinderTest {

    @Test
    void findRingsInMolecule() {
        Collection<? extends LeafSubstructure> targetLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .parse().getAllLeafSubstructures();
        MoleculeGraph moleculeGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) targetLeafSubstructure.iterator().next());
        List<Set<MoleculeAtom>> rings = MoleculeRingFinder.of(moleculeGraph);
        assertEquals(3, rings.size());
        assertEquals(5, rings.get(0).size());
        assertEquals(5, rings.get(1).size());
        assertEquals(6, rings.get(2).size());
    }
}