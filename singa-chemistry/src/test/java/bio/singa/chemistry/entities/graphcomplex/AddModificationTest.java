package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        GraphComplex second = GraphComplex.from(b, bindingSite);
        List<GraphComplex> results = new ArrayList<>();
        AddModification addModification = new AddModification(second, bindingSite);
        addModification.setResultList(results);
        addModification.apply(first);

        assertEquals(first.getNodes().size(), 1);
        assertEquals(second.getNodes().size(), 1);
        assertEquals(results.size(), 1);
        GraphComplex graphComplex = results.get(0);
        assertEquals(graphComplex.getNodes().size(), 2);
        assertTrue(graphComplex.containsEntity(a));
        assertTrue(graphComplex.containsEntity(b));
    }
}