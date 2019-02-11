package bio.singa.simulation.export.format;

import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.concentration.imlementations.*;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static bio.singa.features.model.Evidence.SourceType.PREDICTION;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MILLI_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.Units.MINUTE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class FormatReactionEquationTest {

    @Test
    void testReactionStringFormat() {
        Simulation simulation = new Simulation();

        Enzyme enzyme = new Enzyme.Builder("E")
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), Evidence.NO_EVIDENCE))
                .assignFeature(new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), Evidence.NO_EVIDENCE))
                .build();
        SmallMolecule substrate = SmallMolecule.create("A").build();
        SmallMolecule product1 = SmallMolecule.create("B").build();
        SmallMolecule product2 = SmallMolecule.create("C").build();

        MichaelisMentenReaction reaction = MichaelisMentenReaction.inSimulation(simulation)
                .enzyme(enzyme)
                .addSubstrate(substrate)
                .addProduct(product1)
                .addProduct(product2)
                .build();

        assertEquals("\\ch{A ->[ E ] B + C}", FormatReactionEquation.formatTex(reaction));
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

        NthOrderReaction reaction = NthOrderReaction.inSimulation(simulation)
                .addSubstrate(substrate1, 2)
                .addSubstrate(substrate2)
                .addProduct(product1)
                .addProduct(product2)
                .rateConstant(k)
                .build();

        assertEquals("\\ch{ 2 A + B -> C + D}", FormatReactionEquation.formatTex(reaction));

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

        ReversibleReaction reaction = ReversibleReaction.inSimulation(simulation)
                .addSubstrate(substrate)
                .addProduct(product)
                .forwardsRateConstant(kf)
                .backwardsRateConstant(kb)
                .build();

        assertEquals("\\ch{A <=> B}", FormatReactionEquation.formatTex(reaction));

    }

    @Test
    void testSectionReactionFormat() {
        Simulation simulation = new Simulation();

        SmallMolecule ligand = SmallMolecule.create("L").build();
        Protein receptor = new Protein.Builder("R").build();
        ComplexEntity complex = ComplexEntity.from(ligand, receptor);

        RateConstant kf = RateConstant.create(2.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        SectionDependentReaction reaction = SectionDependentReaction.inSimulation(simulation)
                .addSubstrate(ligand, CellTopology.OUTER)
                .addSubstrate(receptor, CellTopology.MEMBRANE)
                .addProduct(complex, CellTopology.MEMBRANE)
                .forwardsRate(kf)
                .backwardsRate(kb)
                .build();

        assertEquals("\\ch{L$_o$ + R$_m$ <=> L:R$_m$}", FormatReactionEquation.formatTex(reaction));
    }

    @Test
    void testComplexBuildingReactionFormat() {
        Simulation simulation = new Simulation();

        Protein binder = new Protein.Builder("binder").build();
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee").build();
        ComplexEntity complex = ComplexEntity.from(bindee, binder);

        RateConstant kf = RateConstant.create(2.0)
                .forward().secondOrder()
                .concentrationUnit(MILLI_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ComplexBuildingReaction reaction = ComplexBuildingReaction.inSimulation(simulation)
                .of(bindee, kf)
                .in(CellTopology.INNER)
                .by(binder, kb)
                .to(CellTopology.MEMBRANE)
                .formingComplex(complex)
                .build();

        assertEquals("\\ch{L$_o$ + R$_m$ <=> (L,R)$_m$}", FormatReactionEquation.formatTex(reaction));
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

        DynamicReaction reaction = DynamicReaction.inSimulation(simulation)
                .identifier("Adenylate Cyclase Reaction")
                // derived from v = kCat*E*S/(kM+s), where kCat has is a pseudo first order rate derived from regression
                // the scaling parameter determines the unit of the equation
                .kineticLaw("(1/((p01/cATPuM)^p02+(p03/cGAuM)^p04))*us*cAC6*(cATP/(kM+cATP))")
                // matlab regression
                .referenceParameter("p01", 6.144e5)
                .referenceParameter("p02", 0.3063)
                .referenceParameter("p03", 1.196)
                .referenceParameter("p04", 1.153)
                .referenceParameter("us", scaling)
                .referenceParameter("kM", UnitRegistry.scale(km.getContent()).getValue().doubleValue())
                .referenceParameter("cATPuM", new Reactant(new SmallMolecule.Builder("ATP").build(), ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cGAuM", new Reactant(new SmallMolecule.Builder("GAT").build(), ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cAC6", new Reactant(new SmallMolecule.Builder("AC6").build(), ReactantRole.CATALYTIC, CellTopology.MEMBRANE))
                .referenceParameter("cATP", new Reactant(new SmallMolecule.Builder("ATP").build(), ReactantRole.SUBSTRATE))
                .referenceParameter(new Reactant(new SmallMolecule.Builder("CAMP").build(), ReactantRole.PRODUCT))
                .build();

        assertEquals("\\ch{binder$_m$ + bindee$_i$ <=> (bindee,binder)$_m$}", FormatReactionEquation.formatTex(reaction));
    }

}