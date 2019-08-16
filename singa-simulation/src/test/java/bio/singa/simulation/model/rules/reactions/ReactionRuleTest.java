package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.*;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.SUBSTRATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class ReactionRuleTest {

    private static ChemicalEntity akap;

    private static ChemicalEntity pkar;

    private static ModificationSite pkarSite1;
    private static ModificationSite pkarSite2;
    private static ModificationSite campSite1;
    private static ModificationSite campSite2;
    private static ChemicalEntity camp;

    private static ChemicalEntity pkac;

    private static ModificationSite atpSite;
    private static ChemicalEntity atp;
    private static ChemicalEntity adp;

    private static ModificationSite pkarPSite;
    private static ModificationSite pdePSite;
    private static ModificationSite aqpPSite;

    private static ChemicalEntity p;

    private static ModificationSite substrateSite;
    private static ChemicalEntity pde;
    private static ChemicalEntity aqp;

    @BeforeAll
    static void initialize() {
        akap = Protein.create("AKAP")
                .membraneBound()
                .build();
        pkarSite1 = ModificationSite.create("pkar1").build();
        pkarSite2 = ModificationSite.create("pkar2").build();
        pkar = Protein.create("PKAR").build();
        campSite1 = ModificationSite.create("camp1").build();
        campSite2 = ModificationSite.create("camp2").build();
        pkac = Protein.create("PKAC").build();
        atpSite = ModificationSite.create("atp").build();
        atp = SmallMolecule.create("ATP").build();
        adp = SmallMolecule.create("ADP").build();
        pkarPSite = ModificationSite.create("ppkar").build();
        pdePSite = ModificationSite.create("ppde").build();
        aqpPSite = ModificationSite.create("paqp").build();
        p = SmallMolecule.create("P").build();
        substrateSite = ModificationSite.create("sub").build();
        pde = Protein.create("PDE").build();
        aqp = Protein.create("AQP")
                .membraneBound()
                .build();
        camp = SmallMolecule.create("CAMP").build();
    }

    @Test
    void testBinding() {
        ReactionRule akapBindsPkar = ReactionRule.create()
                .entity(akap)
                .binds(pkar, pkarSite1)
                .build();

        ReactionNetworkGenerator generator = new ReactionNetworkGenerator();
        List<ReactantSet> reactantSets = generator.addRule(akapBindsPkar);
        generator.generateNetwork();

        assertEquals(1, reactantSets.size());

        assertEquals(2, reactantSets.get(0).getSubstrates().size());
        Reactant first = reactantSets.get(0).getSubstrates().get(0);
        assertEquals(ComplexEntity.from(akap, pkarSite1), first.getEntity());
        assertEquals(SUBSTRATE, first.getRole());

        Reactant second = reactantSets.get(0).getSubstrates().get(1);
        assertEquals(ComplexEntity.from(pkar, pkarSite1), second.getEntity());
        assertEquals(SUBSTRATE, second.getRole());

        Reactant third = reactantSets.get(0).getProducts().get(0);
        assertEquals(EntityRegistry.matchExactly("AKAP", "PKAR"), third.getEntity());
        assertEquals(PRODUCT, third.getRole());
    }

    @Test
    @Disabled
    void createSomeRules() {

        ReactionNetworkGenerator aggregator = new ReactionNetworkGenerator();

        ReactionRule akapBindsPkar = ReactionRule.create()
                .entity(akap)
                .binds(pkar, pkarSite1)
                .identifier("akap pkar binding")
                .build();
        akapBindsPkar.setProductsOnly(true);
        aggregator.addRule(akapBindsPkar);

        ReactionRule pkacBindsPkar = ReactionRule.create()
                .entity(pkac)
                .binds(pkar, pkarSite2)
                .identifier("pkac pkar binding")
                .build();
        pkacBindsPkar.setProductsOnly(true);
        aggregator.addRule(pkacBindsPkar);

        ReactionRule pkarBindsCamp1 = ReactionRule.create()
                .entity(pkar)
                .binds(camp, campSite1)
                .targetCondition(ReactantCondition
                        .hasNotPart(camp))
                .identifier("pkar camp binding 1")
                .build();
        aggregator.addRule(pkarBindsCamp1);

        ReactionRule pkarBindsCamp2 = ReactionRule.create()
                .entity(pkar)
                .binds(camp, campSite2)
                .targetCondition(ReactantCondition
                        .hasPart(camp))
                .identifier("pkar camp binding 2")
                .build();
        aggregator.addRule(pkarBindsCamp2);

        ReactionRule pkarReleaseCamp2 = pkarBindsCamp2.invertRule();
        aggregator.addRule(pkarReleaseCamp2);

        ReactionRule pkacAtpBinding = ReactionRule.create()
                .entity(pkac)
                .binds(atp, atpSite)
                .targetCondition(ReactantCondition
                        .hasNumerOfPart(camp, 2))
                .targetCondition(ReactantCondition
                        .isUnoccupied(substrateSite))
                .identifier("pkac atp binding")
                .build();
        aggregator.addRule(pkacAtpBinding);

        ReactionRule pkacAutophosphorylation = ReactionRule.create()
                .entity(pkar)
                .adds(p, pkarPSite)
                .targetCondition(ReactantCondition
                        .hasPart(atp))
                .targetCondition(ReactantCondition
                        .hasNotPart(p))
                .andModification()
                .remove(atp, atpSite)
                .andModification()
                .produce(adp)
                .identifier("pka autophosphorylation")
                .build();
        aggregator.addRule(pkacAutophosphorylation);

        ReactionRule pkacAqpSubstrateBinding = ReactionRule.create()
                .entity(pkac)
                .binds(aqp, substrateSite)
                .targetCondition(ReactantCondition
                        .hasPart(atp))
                .targetCondition(ReactantCondition
                        .hasNumerOfPart(p, 1))
                .identifier("pkac aqp binding")
                .build();
        aggregator.addRule(pkacAqpSubstrateBinding);

        ReactionRule pkacPdeSubstrateBinding = ReactionRule.create()
                .entity(pkac)
                .binds(pde, substrateSite)
                .targetCondition(ReactantCondition
                        .hasPart(atp))
                .targetCondition(ReactantCondition
                        .hasPart(p))
                .identifier("pkac pde binding")
                .build();
        aggregator.addRule(pkacPdeSubstrateBinding);

        ReactionRule pkacAqpPhosphorylation = ReactionRule.create()
                .entity(aqp)
                .adds(p, aqpPSite)
                .targetCondition(ReactantCondition
                        .hasPart(pkac))
                .andModification()
                .remove(atp, atpSite)
                .andModification()
                .produce(adp)
                .identifier("pkac aqp phosphorylation")
                .build();
        aggregator.addRule(pkacAqpPhosphorylation);

        ReactionRule pkacPdePhosphorylation = ReactionRule.create()
                .entity(pde)
                .adds(p, pdePSite)
                .targetCondition(ReactantCondition
                        .hasPart(pkac))
                .andModification()
                .remove(atp, atpSite)
                .andModification()
                .produce(adp)
                .identifier("pkac pde phosphorylation")
                .build();
        aggregator.addRule(pkacPdePhosphorylation);

        ReactionRule aqpRelease = ReactionRule.create()
                .entity(pkac)
                .release(aqp, substrateSite)
                .targetCondition(ReactantCondition
                        .hasNumerOfPart(p, 2))
                .identifier("aqpp release")
                .build();
        aggregator.addRule(aqpRelease);

        ReactionRule pdeRelease = ReactionRule.create()
                .entity(pkac)
                .release(pde, substrateSite)
                .targetCondition(ReactantCondition
                        .hasNumerOfPart(p, 2))
                .identifier("pdep release")
                .build();
        aggregator.addRule(pdeRelease);

        aggregator.generateNetwork();
    }
}