package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static bio.singa.chemistry.entities.graphcomplex.conditions.CandidateConditionBuilder.hasNumberOfEntity;
import static bio.singa.chemistry.entities.graphcomplex.conditions.CandidateConditionBuilder.hasOccupiedBindingSite;

/**
 * @author cl
 */
class ReactionChainTest {

    private static Protein akap;
    private static Protein pkar;
    private static Protein pkac;
    private static SmallMolecule p;
    private static SmallMolecule atp;
    private static SmallMolecule camp;

    @BeforeAll
    static void initialize() {
        akap = Protein.create("AKAP")
                .membraneBound()
                .build();
        pkar = Protein.create("PKAR").build();
        pkac = Protein.create("PKAC").build();
        p = SmallMolecule.create("P").build();
        atp = SmallMolecule.create("ATP").build();
        camp = SmallMolecule.create("CAMP").build();
    }

    @Test
    void testAdd() {
        ReactionChain reactors = ReactionChainBuilder.add(akap)
                .to(pkar)
                .build();
    }

    @Test
    void testBind() {
        ReactionChain reactors = ReactionChainBuilder.bind(akap)
                .to(pkar)
                .build();

        ReactionNetworkGenerator generator = new ReactionNetworkGenerator();
        generator.add(reactors);
        generator.generate();
    }

    @Test
    void testRemove() {
        ReactionChain reactors = ReactionChainBuilder.remove(akap)
                .from(pkar)
                .build();
    }

    @Test
    void testRelease() {
        ReactionChain reactors = ReactionChainBuilder.release(akap)
                .from(pkar)
                .build();
    }

    @Test
    void chainReactors1() {
        ReactionChain reactors = ReactionChainBuilder.add(p)
                .to(pkar)
                .condition(hasNumberOfEntity(atp, 1))
                .and()
                .remove(atp)
                .from(pkar)
                .build();
        System.out.println();
    }

    @Test
    void chainReactors2() {
        ReactionChain reactors = ReactionChainBuilder.add(p)
                .to(pkar)
                .condition(hasNumberOfEntity(atp, 1))
                .identifier("blah")
                .and()
                .remove(atp)
                .from(pkar)
                .and()
                .release(pkac)
                .from(pkar)
                .build();
    }

    @Test
    void testNetworkGeneration() {
        ReactionNetworkGenerator rng = new ReactionNetworkGenerator();

        ReactionChain akapBinding = ReactionChainBuilder.bind(pkar)
                .to(akap)
                .identifier("pka activation: akap pkar binding")
                .build();
        rng.add(akapBinding);

        ReactionChain pkarBinding = ReactionChainBuilder.bind(pkac)
                .to(pkar)
                .identifier("pka activation: akap pkar binding")
                .build();
        rng.add(pkarBinding);

        BindingSite camp1 = BindingSite.createNamed("camp1");
        BindingSite camp2 = BindingSite.createNamed("camp2");

        ReactionChain camp1Binding = ReactionChainBuilder.bind(camp1, camp)
                .to(pkar)
                .identifier("pka activation: pkar camp pocket a binding")
                .build();
        rng.add(camp1Binding);

        ReactionChain camp2Binding = ReactionChainBuilder.bind(camp2, camp)
                .to(pkar)
                .secondaryCondition(hasOccupiedBindingSite(camp1))
                .identifier("pka activation: pkar camp pocket b binding")
                .build();
        rng.add(camp2Binding);

        ReactionChain atpBinding = ReactionChainBuilder.bind(atp)
                .to(pkac)
                .secondaryCondition(hasOccupiedBindingSite(camp1))
                .secondaryCondition(hasOccupiedBindingSite(camp2))
                .identifier("pka activation: pkac ATP binding")
                .build();
        rng.add(atpBinding);

        BindingSite pkarPSite = BindingSite.createNamed("s96");

        ReactionChain autophoshorylation = ReactionChainBuilder.add(pkarPSite, p)
                .to(pkar)
                .condition(hasNumberOfEntity(pkac, 1))
                .condition(hasNumberOfEntity(atp, 1))
                .and()
                .remove(atp)
                .from(pkac)
                .and()
                .release(pkac)
                .from(akap)
                .identifier("pka activation: pkac pkar autophosphorylation")
                .build();
        rng.add(autophoshorylation);

        rng.generate();
        System.out.println();

    }




}