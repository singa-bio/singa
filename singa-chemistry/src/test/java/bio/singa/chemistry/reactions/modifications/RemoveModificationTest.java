package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class RemoveModificationTest {

    private static Protein a = Protein.create("A").build();
    private static SmallMolecule b = SmallMolecule.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        GraphComplex first = GraphComplex.from(a, bindingSite);
        GraphComplex second = GraphComplex.from(b, bindingSite);
        GraphComplex complex = first.bind(second, bindingSite).get();

        ComplexEntityModification modification = new RemoveModification(bindingSite, b);
        modification.addCandidate(complex);
        modification.apply();
        List<GraphComplex> results = modification.getResults();

        assertEquals(2, complex.getNodes().size());
        assertEquals(1, results.size());
        GraphComplex graphComplex = results.get(0);
        assertEquals(1, graphComplex.getNodes().size());
        assertTrue(graphComplex.containsEntity(a));
        assertFalse(graphComplex.containsEntity(b));

    }

}