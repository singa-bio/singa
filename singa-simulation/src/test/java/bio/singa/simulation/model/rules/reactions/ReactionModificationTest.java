package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static bio.singa.simulation.model.rules.reactions.ModificationOperation.ADD;
import static bio.singa.simulation.model.rules.reactions.ModificationOperation.REMOVE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class ReactionModificationTest {

    private static ChemicalEntity akap;
    private static ChemicalEntity pkac;
    private static ChemicalEntity atp;
    private static ChemicalEntity adp;
    private static ModificationSite m1;
    private static ModificationSite m2;

    @BeforeAll
    static void initialize() {
        akap = Protein.create("AKAP").build();
        pkac = Protein.create("PKAC").build();
        atp = SmallMolecule.create("ATP").build();
        adp = SmallMolecule.create("ADP").build();
        m1 = ModificationSite.create("m1").build();
        m2 = ModificationSite.create("m2").build();
    }

    @Test
    void testSimpleTargetAddition() {
        ReactantModification entityModification = new ReactantModification(pkac, atp, m1, ADD);
        ComplexEntity target = entityModification.getTargetSite();
        ChemicalEntity complex = entityModification.apply(target);

        assertEquals(ComplexEntity.from(pkac, m1), target);
        assertEquals(ComplexEntity.from(pkac, ComplexEntity.from(m1, atp)), complex);
    }

    @Test
    void testComplexTargetAddition() {
        ReactantModification atpModification = new ReactantModification(pkac, atp, m1, ADD);
        ComplexEntity atpTarget = atpModification.getTargetSite();

        ReactantModification akapModification = new ReactantModification(atpTarget, akap, m2, ADD);
        ComplexEntity akapTarget = akapModification.getTargetSite();
        ComplexEntity akapComplex = akapModification.apply(akapTarget);
        ComplexEntity multiComplex = atpModification.apply(akapComplex);

        assertNotNull(multiComplex.find(ComplexEntity.from(m1, atp)));
        assertNotNull(multiComplex.find(ComplexEntity.from(m2, akap)));
    }

    @Test
    void testRemoval() {
        ReactantModification atpRemoval = new ReactantModification(pkac, atp, m1, REMOVE);
        ComplexEntity target = ComplexEntity.from(m1, atp, pkac);
        ComplexEntity complex = atpRemoval.apply(target);

        assertNull(complex.find(ComplexEntity.from(m1, atp)));
        assertNotNull(complex.find(m1));
    }

}