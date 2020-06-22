package bio.singa.simulation.entities;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.ComplexEntityBuilder;
import bio.singa.simulation.entities.SimpleEntity;
import org.junit.jupiter.api.Test;

/**
 * @author cl
 */
class ComplexEntityBuilderTest {

    @Test
    void testSimpleSnap() {
        ChemicalEntity a = SimpleEntity.create("A").build();
        ChemicalEntity b = SimpleEntity.create("B").build();
        ComplexEntity complexEntity = ComplexEntityBuilder.create()
                .combine(a, b)
                .build();
        System.out.println(complexEntity);
    }

    @Test
    void testMoreSnap() {
        ChemicalEntity a = SimpleEntity.create("A").build();
        ChemicalEntity b = SimpleEntity.create("B").build();
        ChemicalEntity c = SimpleEntity.create("C").build();
        ComplexEntity complexEntity = ComplexEntityBuilder.create()
                .combine(a, b)
                .combine(b, c)
                .build();
        System.out.println(complexEntity);
    }

}