package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseModificationTest {

    private static Protein a = Protein.create("A").build();
    private static SmallMolecule b = SmallMolecule.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        GraphComplex first = GraphComplex.from(a, bindingSite);
        GraphComplex second = GraphComplex.from(b, bindingSite);
        GraphComplex complex = first.bind(second, bindingSite).get();

        ComplexEntityModification modification = new ReleaseModification(bindingSite);
        modification.addCandidate(complex);
        modification.apply();
        List<GraphComplex> results = modification.getResults();

        assertEquals(2, complex.getNodes().size());
        assertEquals(2, results.size());
        GraphComplex firstResult = results.get(0);
        assertEquals(1, firstResult.getNodes().size());
        assertTrue(firstResult.containsEntity(a));
        assertFalse(firstResult.containsEntity(b));

        GraphComplex secondResult = results.get(1);
        assertEquals(1, secondResult.getNodes().size());
        assertFalse(secondResult.containsEntity(a));
        assertTrue(secondResult.containsEntity(b));

    }

}