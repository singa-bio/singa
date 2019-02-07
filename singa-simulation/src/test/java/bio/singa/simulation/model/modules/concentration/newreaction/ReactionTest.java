package bio.singa.simulation.model.modules.concentration.newreaction;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static bio.singa.simulation.model.sections.CellSubsections.CELL_OUTER_MEMBRANE;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class ReactionTest {


    @Test
    void irreversibleReactionCytoplasm() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b)
                .addProduct(c)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
        }

    }

    @Test
    void irreversibleReactionMembrane() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CELL_OUTER_MEMBRANE, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b, CellTopology.MEMBRANE)
                .addProduct(c, CellTopology.MEMBRANE)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CellTopology.MEMBRANE, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CellTopology.MEMBRANE, c));
        }
    }

    @Test
    void irreversibleReactionStoichiometry() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b, 2)
                .addProduct(c)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
        }
    }

    @Test
    void irreversibleReactionVesicle() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b)
                .addProduct(c)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
        }

    }

    @Test
    void irreversibleReactionOld() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(a)
                .addSubstrate(b, 2)
                .addProduct(c)
                .rateConstant(rate)
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
        }

    }


}