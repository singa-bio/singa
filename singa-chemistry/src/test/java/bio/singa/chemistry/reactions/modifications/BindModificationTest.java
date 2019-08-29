package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BindModificationTest {

    private static Protein a = Protein.create("A").build();
    private static SmallMolecule b = SmallMolecule.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        GraphComplex first = GraphComplex.from(a, bindingSite);
        GraphComplex second = GraphComplex.from(b, bindingSite);

        ComplexEntityModification modification = new BindModification(bindingSite);
        modification.addCandidate(first);
        modification.addCandidate(second);
        modification.apply();
        List<GraphComplex> results = modification.getResults();

        assertEquals(1, first.getNodes().size());
        assertEquals(1, second.getNodes().size());
        assertEquals(1, results.size());
        GraphComplex graphComplex = results.get(0);
        assertEquals(2, graphComplex.getNodes().size());
        assertTrue(graphComplex.containsEntity(a));
        assertTrue(graphComplex.containsEntity(b));
    }

}