package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.SimpleEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class RemoveModificationTest {

    private static ChemicalEntity a = SimpleEntity.create("A").build();
    private static ChemicalEntity b = SimpleEntity.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        ComplexEntity first = ComplexEntity.from(a, bindingSite);
        ComplexEntity second = ComplexEntity.from(b, bindingSite);
        ComplexEntity complex = first.bind(second, bindingSite).get();

        ComplexEntityModification modification = new RemoveModification(bindingSite, b);
        modification.addCandidate(complex);
        modification.apply();
        List<ComplexEntity> results = modification.getResults();

        assertEquals(2, complex.getNodes().size());
        assertEquals(1, results.size());
        ComplexEntity graphComplex = results.get(0);
        assertEquals(1, graphComplex.getNodes().size());
        assertTrue(graphComplex.containsEntity(a));
        assertFalse(graphComplex.containsEntity(b));

    }

}