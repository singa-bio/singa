package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class RemoveModficationTest {

    private static Protein a = Protein.create("A").build();
    private static SmallMolecule b = SmallMolecule.create("B").build();

    @Test
    void apply() {
        BindingSite bindingSite = BindingSite.forPair(a, b);
        GraphComplex first = GraphComplex.from(a, bindingSite);
        GraphComplex second = GraphComplex.from(b, bindingSite);
        GraphComplex complex = first.bind(second, bindingSite).get();
        List<GraphComplex> results = new ArrayList<>();
        RemoveModfication removeModfication = new RemoveModfication(b, bindingSite);
        removeModfication.setResultList(results);
        removeModfication.apply(complex);

        assertEquals(complex.getNodes().size(), 2);
        assertEquals(results.size(), 1);
        GraphComplex graphComplex = results.get(0);
        assertEquals(graphComplex.getNodes().size(), 1);
        assertTrue(graphComplex.containsEntity(a));
        assertFalse(graphComplex.containsEntity(b));

    }



}