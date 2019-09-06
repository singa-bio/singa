package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.reactions.ReactionNetworkGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder.*;

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
    private static Protein aqp;
    private static Protein pde;
    private static Protein pp2b;

    @BeforeAll
    static void initialize() {
        akap = Protein.create("AKAP")
                .membraneBound()
                .build();
        pkar = Protein.create("PKAR").build();
        pkac = Protein.create("PKAC").build();
        aqp = Protein.create("AQP2").build();
        pde = Protein.create("PDE").build();
        pp2b = Protein.create("PP2B").build();

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
    }

    @Test
    void chainReactors2() {
        ReactionChain reactors = ReactionChainBuilder.add(p)
                .to(pkar)
                .condition(hasNumberOfEntity(atp, 1))
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

        BindingSite camp1 = BindingSite.createNamed("pkar-camp1");
        BindingSite camp2 = BindingSite.createNamed("pkar-camp2");
        BindingSite pkarPSite = BindingSite.createNamed("pkar-s96");
        BindingSite aqpPSite = BindingSite.createNamed("aqp2-s256");
        BindingSite pdePSite = BindingSite.createNamed("pde4-s54");
        BindingSite pkacSubstrate = BindingSite.createNamed("pkac-substrate");
        BindingSite pp2bSubstrate = BindingSite.createNamed("pp2b-substrate");

        ReactionChain akapBinding = ReactionChainBuilder.bind(pkar)
                //.primaryCondition(hasUnoccupiedBindingSite(pkarPSite))
                .to(akap)
                .identifier("pka activation: akap pkar binding")
                .build();
        rng.addPreReaction(akapBinding);

        ReactionChain pkarBinding = ReactionChainBuilder.bind(pkac)
                .primaryCondition(hasUnoccupiedBindingSite(pkacSubstrate))
                .to(pkar)
                .secondaryCondition(hasNoneOfEntity(p))
                .identifier("pka activation: pkar pkac binding")
                .build();
        rng.add(pkarBinding);

        ReactionChain camp1Binding = ReactionChainBuilder.bind(camp1, camp)
                .to(pkar)
                .identifier("pka activation: pkar camp pocket a binding")
                .build();
        rng.add(camp1Binding);

        ReactionChain camp2Binding = ReactionChainBuilder.bind(camp2, camp)
                .to(pkar)
                .secondaryCondition(hasOccupiedBindingSite(camp1))
                .secondaryCondition(hasNoneOfEntity(pp2b))
                .identifier("pka activation: pkar camp pocket b binding")
                .considerInversion()
                .build();
        rng.add(camp2Binding);

        ReactionChain atpBinding = ReactionChainBuilder.bind(atp)
                .to(pkac)
                .secondaryCondition(hasNoneOfEntity(pp2b))
                .secondaryCondition(hasNoneOfEntity(pkar))
                .identifier("pka activation: pkac ATP binding")
                .build();
        rng.add(atpBinding);

        ReactionChain atpAutoBinding = ReactionChainBuilder.bind(atp)
                .to(pkac)
                .secondaryCondition(hasNumberOfEntity(pkar, 1))
                .secondaryCondition(hasOccupiedBindingSite(camp1))
                .secondaryCondition(hasOccupiedBindingSite(camp2))
                .identifier("pka activation: pkac auophosphorylation ATP binding")
                .build();
        rng.add(atpAutoBinding);

        ReactionChain autophoshorylation = ReactionChainBuilder.add(pkarPSite, p)
                .to(pkar)
                .condition(hasNumberOfEntity(pkac, 1))
                .condition(hasNumberOfEntity(atp, 1))
                .and()
                .remove(atp)
                .from(pkac)
                .and()
                .release(pkac)
                .from(pkar)
                .identifier("pka activation: pkac pkar autophosphorylation")
                .build();
        rng.add(autophoshorylation);

        ReactionChain aqpBind = ReactionChainBuilder.bind(pkacSubstrate, aqp)
                .primaryCondition(hasUnoccupiedBindingSite(aqpPSite))
                .to(pkac)
                .secondaryCondition(hasNumberOfEntity(atp, 1))
                .secondaryCondition(hasNoneOfEntity(pkar))
                .identifier("pka phosphorylation: aqp binding")
                .build();
        rng.add(aqpBind);

        ReactionChain pdeBind = ReactionChainBuilder.bind(pkacSubstrate, pde)
                .primaryCondition(hasUnoccupiedBindingSite(pdePSite))
                .to(pkac)
                .secondaryCondition(hasNumberOfEntity(atp, 1))
                .secondaryCondition(hasNoneOfEntity(pkar))
                .identifier("pka phosphorylation: pde binding")
                .build();
        rng.add(pdeBind);

        ReactionChain pdePhosphorlyation = ReactionChainBuilder.add(pdePSite, p)
                .to(pde)
                .condition(hasNumberOfEntity(pkac, 1))
                .and()
                .remove(atp)
                .from(pkac)
                .and()
                .release(pkacSubstrate, pde)
                .from(pkac)
                .identifier("pka activation: pde phosphorylation")
                .build();
        rng.add(pdePhosphorlyation);

        ReactionChain aqpPhosphorylation = ReactionChainBuilder.add(aqpPSite, p)
                .to(aqp)
                .condition(hasNumberOfEntity(pkac, 1))
                .and()
                .remove(atp)
                .from(pkac)
                .and()
                .release(pkacSubstrate, aqp)
                .from(pkac)
                .identifier("pka activation: aqp2 phosphorylation")
                .build();
        rng.add(aqpPhosphorylation);

        ReactionChain pp2bPdeBinding = ReactionChainBuilder.bind(pp2bSubstrate, pde)
                .primaryCondition(hasOccupiedBindingSite(pdePSite))
                .to(pp2b)
                .secondaryCondition(hasNoMoreThanNumberOfPartners(pp2b, 0))
                .identifier("pp2b dephosphorylation: pdep binding")
                .build();
        rng.add(pp2bPdeBinding);

        ReactionChain pdeDephosphorylation = ReactionChainBuilder.remove(pdePSite, p)
                .from(pde)
                .condition(hasNumberOfEntity(pp2b, 1))
                .and()
                .release(pp2bSubstrate, pp2b)
                .from(pde)
                .identifier("pp2b dephosphorylation: pdep dephosphorylation")
                .build();
        rng.add(pdeDephosphorylation);

        ReactionChain pp2bAqpBinding = ReactionChainBuilder.bind(pp2bSubstrate, aqp)
                .primaryCondition(hasOccupiedBindingSite(aqpPSite))
                .to(pp2b)
                .secondaryCondition(hasNoMoreThanNumberOfPartners(pp2b, 0))
                .identifier("pp2b dephosphorylation: aqp binding")
                .build();
        rng.add(pp2bAqpBinding);

        ReactionChain aqpDephosphorylation = ReactionChainBuilder.remove(aqpPSite, p)
                .from(aqp)
                .condition(hasNumberOfEntity(pp2b, 1))
                .and()
                .release(pp2bSubstrate, pp2b)
                .from(aqp)
                .identifier("pp2b dephosphorylation: aqp dephosphorylation")
                .build();
        rng.add(aqpDephosphorylation);

        ReactionChain pp2bpkarBinding = ReactionChainBuilder.bind(pp2bSubstrate, pkar)
                .primaryCondition(hasOccupiedBindingSite(pkarPSite))
                .primaryCondition(hasOneOfEntity(camp))
                .primaryCondition(hasNoneOfEntity(atp))
                .to(pp2b)
                .secondaryCondition(hasNoMoreThanNumberOfPartners(pp2b, 0))
                .identifier("pp2b dephosphorylation: pkar binding")
                .build();
        rng.add(pp2bpkarBinding);

        ReactionChain pkarDephosphorylation = ReactionChainBuilder.remove(pkarPSite, p)
                .from(pkar)
                .condition(hasOneOfEntity(pp2b))
                .and()
                .release(pp2bSubstrate, pp2b)
                .from(pkar)
                .identifier("pp2b dephosphorylation: pkar dephosphorylation")
                .build();
        rng.add(pkarDephosphorylation);

        rng.generate();
        System.out.println();

    }


}