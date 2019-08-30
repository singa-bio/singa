package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
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