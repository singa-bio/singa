package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class AddModificationTest {

    private static Protein a = Protein.create("A").build();
    private static SmallMolecule b = SmallMolecule.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        GraphComplex first = GraphComplex.from(a, bindingSite);

        ComplexEntityModification modification = new AddModification(bindingSite, b);
        modification.addCandidate(first);
        modification.apply();
        List<GraphComplex> results = modification.getResults();

        assertEquals(1, first.getNodes().size());
        assertEquals(results.size(), 1);
        GraphComplex graphComplex = results.get(0);
        assertEquals(2, graphComplex.getNodes().size());
        assertTrue(graphComplex.containsEntity(a));
        assertTrue(graphComplex.containsEntity(b));
    }

}