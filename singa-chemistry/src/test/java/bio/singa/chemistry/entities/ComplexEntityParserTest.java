package bio.singa.chemistry.entities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
class ComplexEntityParserTest {

    @Test
    void testSimple() {
        // a reference is needed that contains the entities
        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();
        List<ChemicalEntity> reference = new ArrayList<>();
        reference.add(a);
        reference.add(b);
        reference.add(c);
        // create the entity
        ComplexEntity entity = ComplexEntityParser.parseNewick("((A:B):(A:B))", reference);
        entity.print();
    }

    @Test
    void testMoreComplex() {

    }
}