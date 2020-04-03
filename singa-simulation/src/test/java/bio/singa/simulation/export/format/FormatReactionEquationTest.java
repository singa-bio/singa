package bio.singa.simulation.export.format;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import static bio.singa.features.model.Evidence.SourceType.PREDICTION;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.*;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class FormatReactionEquationTest {

    @Test
    void testMichaelisMentenFormat() {
        Simulation simulation = new Simulation();

        MichaelisConstant michaelisConstant = new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), Evidence.NO_EVIDENCE);
        TurnoverNumber turnoverNumber = new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), Evidence.NO_EVIDENCE);

        SmallMolecule substrate = SmallMolecule.create("A").build();
        SmallMolecule product1 = SmallMolecule.create("B").build();
        SmallMolecule product2 = SmallMolecule.create("C").build();
        Protein enzyme = Protein.create("E").build();

        Reaction reaction = ReactionBuilder.staticReactants(simulation)
                .addSubstrate(substrate)
                .addCatalyst(enzyme)
                .addProduct(product1)
                .addProduct(product2)
                .michaelisMenten()
                .michaelisConstant(michaelisConstant)
                .turnover(turnoverNumber)
                .build();

        assertEquals("$\\frac{k_{\\text{cat}} \\cdot [\\text{E}] \\cdot [\\text{A}]}{K_m \\cdot [\\text{A}]}$", FormatReactionKinetics.formatTex(reaction).get(0));
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

        Reaction reaction = ReactionBuilder.staticReactants(simulation)
                .addSubstrate(substrate1, 2)
                .addSubstrate(substrate2)
                .addProduct(product1)
                .addProduct(product2)
                .irreversible()
                .rate(k)
                .build();

        assertEquals("\\ch{ 2 A + B -> C + D}", FormatReactionEquation.formatTex(reaction).get(0));
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

        Reaction reaction = ReactionBuilder.staticReactants(simulation)
                .addSubstrate(substrate, MEMBRANE)
                .addProduct(product, OUTER)
                .reversible()
                .forwardReactionRate(kf)
                .backwardReactionRate(kb)
                .build();

        assertEquals("\\ch{A <=> B}", FormatReactionEquation.formatTex(reaction).get(0));

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

        Reaction reaction = ReactionBuilder.staticReactants(simulation)
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
                .referenceParameter("cATPuM", new Reactant(SmallMolecule.create("ATP").build(), CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cGAuM", new Reactant(SmallMolecule.create("GAT").build(), CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cAC6", new Reactant(SmallMolecule.create("AC6").build(), CATALYTIC, MEMBRANE))
                .referenceParameter("cATP", new Reactant(SmallMolecule.create("ATP").build(), SUBSTRATE))
                .referenceParameter(new Reactant(SmallMolecule.create("CAMP").build(), PRODUCT))
                .identifier("Adenylate Cyclase Reaction")
                .build();

        assertEquals("\\ch{ATP -> [ ATP, GAT, AC 6  ] CAMP}", FormatReactionEquation.formatTex(reaction).get(0));
    }

}