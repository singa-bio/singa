package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.SimpleEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class AddModificationTest {

    private static ChemicalEntity a = SimpleEntity.create("A").build();
    private static ChemicalEntity b = SimpleEntity.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        ComplexEntity first = ComplexEntity.from(a, bindingSite);

        ComplexEntityModification modification = new AddModification(bindingSite, b);
        modification.addCandidate(first);
        modification.apply();
        List<ComplexEntity> results = modification.getResults();

        assertEquals(1, first.getNodes().size());
        assertEquals(results.size(), 1);
        ComplexEntity graphComplex = results.get(0);
        assertEquals(2, graphComplex.getNodes().size());
        assertTrue(graphComplex.containsEntity(a));
        assertTrue(graphComplex.containsEntity(b));
    }

}