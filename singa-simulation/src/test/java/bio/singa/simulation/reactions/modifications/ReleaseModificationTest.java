package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.SimpleEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseModificationTest {

    private static ChemicalEntity a = SimpleEntity.create("A").build();
    private static ChemicalEntity b = SimpleEntity.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        ComplexEntity first = ComplexEntity.from(a, bindingSite);
        ComplexEntity second = ComplexEntity.from(b, bindingSite);
        ComplexEntity complex = first.bind(second, bindingSite).get();

        ComplexEntityModification modification = new ReleaseModification(bindingSite);
        modification.addCandidate(complex);
        modification.apply();
        List<ComplexEntity> results = modification.getResults();

        assertEquals(2, complex.getNodes().size());
        assertEquals(2, results.size());
        ComplexEntity firstResult = results.get(0);
        assertEquals(1, firstResult.getNodes().size());
        assertTrue(firstResult.containsEntity(a));
        assertFalse(firstResult.containsEntity(b));

        ComplexEntity secondResult = results.get(1);
        assertEquals(1, secondResult.getNodes().size());
        assertFalse(secondResult.containsEntity(a));
        assertTrue(secondResult.containsEntity(b));

    }

}