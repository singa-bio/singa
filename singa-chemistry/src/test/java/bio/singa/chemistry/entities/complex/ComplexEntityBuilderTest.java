package bio.singa.chemistry.entities.complex;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.simple.Protein;
import org.junit.jupiter.api.Test;

/**
 * @author cl
 */
class ComplexEntityBuilderTest {

    @Test
    void testSimpleSnap() {
        ChemicalEntity a = Protein.create("A").build();
        ChemicalEntity b = Protein.create("B").build();
        ComplexEntity complexEntity = ComplexEntityBuilder.create()
                .combine(a, b)
                .build();
        System.out.println(complexEntity);
    }

    @Test
    void testMoreSnap() {
        ChemicalEntity a = Protein.create("A").build();
        ChemicalEntity b = Protein.create("B").build();
        ChemicalEntity c = Protein.create("C").build();
        ComplexEntity complexEntity = ComplexEntityBuilder.create()
                .combine(a, b)
                .combine(b, c)
                .build();
        System.out.println(complexEntity);
    }
}