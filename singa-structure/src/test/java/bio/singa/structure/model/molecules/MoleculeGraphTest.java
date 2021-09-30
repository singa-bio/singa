package bio.singa.structure.model.molecules;

import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.oak.OakLeafSubstructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.ALANINE;
import static bio.singa.structure.model.molecules.MoleculeGraphs.createMoleculeGraphFromStructure;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoleculeGraphTest {

    @Test
    @DisplayName("molecule - identity")
    void shouldEqual() {
        MoleculeGraph graph1 = createMoleculeGraphFromStructure((OakLeafSubstructure) StructuralFamilies.AminoAcids.getPrototype(ALANINE));
        MoleculeGraph graph2 = createMoleculeGraphFromStructure((OakLeafSubstructure) StructuralFamilies.AminoAcids.getPrototype(ALANINE));
        assertEquals(graph1, graph2);
    }

    @Test
    @DisplayName("molecule - copy")
    void shouldCopy() {
        MoleculeGraph graph1 = createMoleculeGraphFromStructure((OakLeafSubstructure) StructuralFamilies.AminoAcids.getPrototype(ALANINE));
        MoleculeGraph copy = (MoleculeGraph) graph1.getCopy();
        assertEquals(graph1, copy);
    }
}