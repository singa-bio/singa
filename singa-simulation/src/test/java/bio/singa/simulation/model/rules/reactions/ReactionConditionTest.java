package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class ReactionConditionTest {

    private static ChemicalEntity akap;
    private static ChemicalEntity pkac;
    private static ChemicalEntity pde;
    private static ChemicalEntity atp;
    private static ChemicalEntity m1;
    private static ChemicalEntity m2;
    private static ChemicalEntity m3;

    @BeforeAll
    static void initialize() {
        akap = Protein.create("AKAP").build();
        pkac = Protein.create("PKAC").build();
        pde = Protein.create("PDE").build();
        atp = SmallMolecule.create("ATP").build();
        m1 = ModificationSite.create("m1").build();
        m2 = ModificationSite.create("m2").build();
        m3 = ModificationSite.create("m3").build();
    }

    @Test
    void testHasPart() {
        ComplexEntity multiComplex = ComplexEntity.from(pkac, ComplexEntity.from(m1, atp), ComplexEntity.from(m2, akap));
        ReactantCondition reactionCondition = ReactantCondition.hasPart(ComplexEntity.from(m1, atp));
        assertTrue(reactionCondition.test(multiComplex));
    }

    @Test
    void testHasNotPart() {
        ComplexEntity multiComplex = ComplexEntity.from(pkac, m1, ComplexEntity.from(m2, akap));
        ReactantCondition reactionCondition = ReactantCondition.hasPart(ComplexEntity.from(m1, atp));
        assertTrue(reactionCondition.test(multiComplex));
    }

}