package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class EntityReducerTest {

    @Test
    @DisplayName("entity collection reduction")
    void shouldReduceCorrectly() {

        SmallMolecule a = SmallMolecule.create("A").build();
        SmallMolecule b = SmallMolecule.create("B").build();
        SmallMolecule c = SmallMolecule.create("C").build();
        SmallMolecule d = SmallMolecule.create("D").build();
        SmallMolecule e = SmallMolecule.create("E").build();

        ComplexedChemicalEntity ab = ComplexedChemicalEntity.from(a, b);
        ComplexedChemicalEntity abc = ComplexedChemicalEntity.from(ab, c);
        ComplexedChemicalEntity abcde = ComplexedChemicalEntity.from(abc, d, e);

        List<ChemicalEntity> firstTest = new ArrayList<>();
        firstTest.add(a);
        firstTest.add(ab);
        firstTest.add(abc);
        firstTest.add(abcde);
        firstTest.add(b);

        List<ChemicalEntity> firstResult = EntityReducer.apply(firstTest, EntityReducer.hasPart(ab), EntityReducer.hasNotPart(e));
        assertTrue(firstResult.contains(ab));
        assertTrue(firstResult.contains(abc));
        assertFalse(firstResult.contains(a));
        assertFalse(firstResult.contains(abcde));
        assertFalse(firstResult.contains(b));

    }
}