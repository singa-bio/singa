package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.reactants.CatalyticReactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.ConcentrationInitializer;
import bio.singa.simulation.model.sections.InitialConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class DynamicReactionTest {


    private static final Logger logger = LoggerFactory.getLogger(DynamicReactionTest.class);

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldPerformDynamicReaction() {

        Environment.reset();
        logger.info("Testing section simple dynamic reaction.");
        // the rate constants

        // the substrate
        ChemicalEntity substrate = new SmallMolecule.Builder("substrate")
                .name("substrate")
                .build();

        // the product
        ChemicalEntity product = new SmallMolecule.Builder("product")
                .name("product")
                .build();

        // the catalytic reactant
        ChemicalEntity catalyt = new SmallMolecule.Builder("catalyt")
                .name("catalyt")
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        RateConstant rateConstant = RateConstant.create(0.1)
                .forward()
                .firstOrder()
                .timeUnit(SECOND)
                .build();

        // create and add module
        DynamicReaction binding = DynamicReaction.inSimulation(simulation)
                .kineticLaw("substrate*sin(catalyt)*k/product")
                .referenceParameter(new StoichiometricReactant(substrate, ReactantRole.DECREASING, INNER))
                .referenceParameter(new StoichiometricReactant(product, ReactantRole.INCREASING, INNER))
                .referenceParameter(new CatalyticReactant(catalyt, ReactantRole.INCREASING, INNER))
                .referenceParameter("k", rateConstant)
                .build();

        // TODO reactant role should be Substrate, Product, Catalytic
        // TODO drop catalytic reactant / stochiometric reactant distinction
        // TODO clean up reaction interface / abstract class (reaction based interface defining default methods?)

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // set concentrations
        AutomatonNode node = automatonGraph.getNode(0, 0);
        node.setCellRegion(CellRegion.CYTOSOL_A);

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(new InitialConcentration(SECTION_A, substrate, Quantities.getQuantity(200, NANO_MOLE_PER_LITRE)));
        ci.addInitialConcentration(new InitialConcentration(SECTION_A, product, Quantities.getQuantity(100, NANO_MOLE_PER_LITRE)));
        ci.addInitialConcentration(new InitialConcentration(SECTION_A, catalyt, Quantities.getQuantity(30, NANO_MOLE_PER_LITRE)));
        simulation.setConcentrationInitializer(ci);

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }
    }

}