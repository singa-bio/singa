package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class ComplexedChemicalEntityTest {

    @Test
    @DisplayName("complex entity dissociation")
    void getAllAssociatedChemicalEntities() {

        SmallMolecule a = SmallMolecule.create("A").build();
        SmallMolecule b = SmallMolecule.create("B").build();
        SmallMolecule c = SmallMolecule.create("C").build();
        SmallMolecule d = SmallMolecule.create("D").build();
        SmallMolecule e = SmallMolecule.create("E").build();

        ComplexedChemicalEntity ab = ComplexedChemicalEntity.from(a, b);
        ComplexedChemicalEntity abc = ComplexedChemicalEntity.from(ab, c);

        ComplexedChemicalEntity abcde = ComplexedChemicalEntity.from(abc, d, e);

        List<ChemicalEntity> buildingBlocks = abcde.getBuildingBlocks();
        assertTrue(buildingBlocks.contains(a));
        assertTrue(buildingBlocks.contains(b));
        assertTrue(buildingBlocks.contains(c));
        assertTrue(buildingBlocks.contains(d));
        assertTrue(buildingBlocks.contains(e));

        Collection<ChemicalEntity> apparentEntities = abcde.getAssociatedChemicalEntities();
        assertTrue(apparentEntities.contains(d));
        assertTrue(apparentEntities.contains(e));
        assertTrue(apparentEntities.contains(abc));

        List<ChemicalEntity> associatedEntities = abcde.getAllAssociatedChemicalEntities();
        assertTrue(associatedEntities.contains(a));
        assertTrue(associatedEntities.contains(b));
        assertTrue(associatedEntities.contains(c));
        assertTrue(associatedEntities.contains(d));
        assertTrue(associatedEntities.contains(e));
        assertTrue(associatedEntities.contains(ab));
        assertTrue(associatedEntities.contains(abc));

    }
}