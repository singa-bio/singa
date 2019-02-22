package bio.singa.simulation.export.format;

import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static bio.singa.features.model.Evidence.SourceType.PREDICTION;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.fail;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.Units.MINUTE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class FormatReactionKineticsTest {

    @Test
    void testMichaelisMentenStringFormat() {
        Simulation simulation = new Simulation();

        Protein enzyme = Protein.create("PDE4")
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), Evidence.NO_EVIDENCE))
                .assignFeature(new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), Evidence.NO_EVIDENCE))
                .build();
        SmallMolecule substrate = SmallMolecule.create("cAMP").build();
        SmallMolecule product = SmallMolecule.create("AMP").build();

//        MichaelisMentenReaction reaction = MichaelisMentenReaction.inSimulation(simulation)
//                .enzyme(enzyme)
//                .addSubstrate(substrate)
//                .addProduct(product)
//                .build();
//
//        assertEquals("$\\frac{k_{\\text{cat}} \\cdot [\\text{PDE4}] \\cdot [\\text{cAMP}]}{K_m \\cdot [\\text{cAMP}]}$", FormatReactionKinetics.formatTex(reaction));
        fail();
    }

    @Test
    void testNthOrderReactionFormat() {
        Simulation simulation = new Simulation();

        SmallMolecule substrate1 = SmallMolecule.create("A").build();
        SmallMolecule substrate2 = SmallMolecule.create("B").build();
        SmallMolecule product1 = SmallMolecule.create("C").build();
        SmallMolecule product2 = SmallMolecule.create("D").build();

        RateConstant k = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

//        ReactionBuilder.staticReactants(simulation)
//                .addSubstrate(substrate1)
//                .addSubstrate(substrate2)
//                .addProduct(product1)
//                .addProduct(product2)
//                .irreversible()
//                .rate(k)
//                .build();
//
//        assertEquals("$k \\cdot [\\text{A}] \\cdot [\\text{B}]$", FormatReactionKinetics.formatTex(reaction));

        fail();

    }

    @Test
    void testReversibleReactionFormat() {
        Simulation simulation = new Simulation();

        SmallMolecule substrate = SmallMolecule.create("A").build();
        SmallMolecule product = SmallMolecule.create("B").build();

        RateConstant kf = RateConstant.create(2.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

//        ReactionBuilder.staticReactants(simulation)
//                .addSubstrate(substrate)
//                .addProduct(product)
//                .reversible()
//                .forwardReactionRate(kf)
//                .backwardReactionRate(kb)
//                .build();
//
//        assertEquals("$k_{1} \\cdot [\\text{A}] - k_{-1} \\cdot [\\text{B}]$", FormatReactionKinetics.formatTex(reaction));
        fail();

    }

    @Test
    void testSectionReactionFormat() {
        Simulation simulation = new Simulation();

        SmallMolecule ligand = SmallMolecule.create("L").build();
        Protein receptor = new Protein.Builder("R").build();
        ComplexEntity complex = ComplexEntity.from(ligand,receptor);

        RateConstant kf = RateConstant.create(2.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

//        SectionDependentReaction reaction = SectionDependentReaction.inSimulation(simulation)
//                .addSubstrate(ligand, CellTopology.OUTER)
//                .addSubstrate(receptor, CellTopology.MEMBRANE)
//                .addProduct(complex, CellTopology.MEMBRANE)
//                .forwardsRate(kf)
//                .backwardsRate(kb)
//                .build();
//
//        assertEquals("$k_{1} \\cdot \\text{[L]}_o \\cdot \\text{[R]}_m - k_{-1} \\cdot \\text{[(L,R)]}_m$", FormatReactionKinetics.formatTex(reaction));

        fail();
    }

    @Test
    void testDynamicReactionFormat() {
        Simulation simulation = new Simulation();

        RateConstant scaling = RateConstant.create(1.0)
                .forward().firstOrder()
                .timeUnit(MINUTE)
                .evidence(new Evidence(PREDICTION, "Unit Scaling", "Scales the velocity to the corresponding unit"))
                .build();

        MichaelisConstant km = new MichaelisConstant(Quantities.getQuantity(20, MICRO_MOLE_PER_LITRE), Evidence.NO_EVIDENCE);

//        ReactionBuilder.staticReactants(simulation)
//                // derived from v = kCat*E*S/(kM+s), where kCat has is a pseudo first order rate derived from regression
//                // the scaling parameter determines the unit of the equation
//                .kineticLaw("(1/((p01/cATPuM)^p02+(p03/cGAuM)^p04))*us*cAC6*(cATP/(kM+cATP))")
//                // matlab regression
//                .referenceParameter("p01", 6.144e5)
//                .referenceParameter("p02", 0.3063)
//                .referenceParameter("p03", 1.196)
//                .referenceParameter("p04", 1.153)
//                .referenceParameter("us", scaling)
//                .referenceParameter("kM", UnitRegistry.scale(km.getContent()).getValue().doubleValue())
//                .referenceParameter("cATPuM", new Reactant(SmallMolecule.create("ATP").build(), ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
//                .referenceParameter("cGAuM", new Reactant(SmallMolecule.create("GAT").build(), ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
//                .referenceParameter("cAC6", new Reactant(SmallMolecule.create("AC6").build(), ReactantRole.CATALYTIC, CellTopology.MEMBRANE))
//                .referenceParameter("cATP", new Reactant(SmallMolecule.create("ATP").build(), ReactantRole.SUBSTRATE))
//                .referenceParameter(new Reactant(SmallMolecule.create("CAMP").build(), ReactantRole.PRODUCT))
//                .identifier("Adenylate Cyclase Reaction")
//                .build();
//
//        assertEquals("not supported", FormatReactionKinetics.formatTex(reaction));
        fail();
    }

}