package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.Diffusion;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static bio.singa.simulation.model.sections.CellSubsections.EXTRACELLULAR_REGION;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.MILLI;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.*;

/**
 * A factory class that can be used to create different examples to test and explore certain aspects to the api.
 *
 * @author cl
 */
public class SimulationExamples {

    private static final Logger logger = LoggerFactory.getLogger(SimulationExamples.class);

    /**
     * This simulation simulates the thermal decomposition of dinitrogen pentaoxide. From: Brauer, G. (2012). Handbook
     * of preparative inorganic chemistry, volume 2. Elsevier. 489–491.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createDecompositionReactionExample() {
        // setup time step size
        UnitRegistry.setTime(Quantities.getQuantity(10.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();
        // get required species
        SmallMolecule dinitrogenPentaoxide = ChEBIParserService.parse("CHEBI:29802");
        SmallMolecule nitrogenDioxide = ChEBIParserService.parse("CHEBI:33101");
        SmallMolecule oxygen = ChEBIParserService.parse("CHEBI:15379");

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize concentration
        ConcentrationBuilder.create(simulation)
                .entity(dinitrogenPentaoxide)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(0.02)
                .unit(MOLE_PER_LITRE)
                .build();

        RateConstant rateConstant = RateConstant.create(0.07)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(dinitrogenPentaoxide, 2)
                .addSubstrate(nitrogenDioxide, 4)
                .addProduct(oxygen)
                .irreversible()
                .rate(rateConstant)
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }


    /**
     * This simulation simulates a equilibrium reaction.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createEquilibriumReactionExample() {
        // setup time step size
        UnitRegistry.setTime(Quantities.getQuantity(10.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();

        // set up arbitrary species
        SmallMolecule speciesA = SmallMolecule.create("A").build();
        SmallMolecule speciesB = SmallMolecule.create("B").build();

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize concentration
        ConcentrationBuilder.create(simulation)
                .entity(speciesA)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(1.0)
                .unit(MOLE_PER_LITRE)
                .build();

        RateConstant forwardsRate = RateConstant.create(10)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant backwardsRate = RateConstant.create(10)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // create reaction
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .reversible()
                .forwardReactionRate(forwardsRate)
                .backwardReactionRate(backwardsRate)
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }

    /**
     * This simulation simulates a Michaelis-Menten reaction, where D-Fructose 1-phosphate is converted to
     * glycerone phosphate and D-glyceraldehyde using fructose bisphosphate aldolase. From: Callens, M. et al. (1991).
     * Kinetic properties of fructose bisphosphate aldolase from Trypanosoma brucei compared to aldolase from rabbit
     * muscle and Staphylococcus aureus. Sabio-RK pdbIdentifier: 28851
     *
     * @return The ready to go simulation.
     */
    public static Simulation createMichaelisMentenReactionExample() {
        // setup time step size
        UnitRegistry.setTime(Quantities.getQuantity(1.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        // get required species
        SmallMolecule fructosePhosphate = ChEBIParserService.parse("CHEBI:18105");
        SmallMolecule glyceronePhosphate = ChEBIParserService.parse("CHEBI:16108");
        SmallMolecule glyceraldehyde = ChEBIParserService.parse("CHEBI:17378");
        Protein aldolase = Protein.create("P07752").build();

        // rates
        MichaelisConstant michaelisConstant = new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), Evidence.NO_EVIDENCE);
        TurnoverNumber turnoverNumber = new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), Evidence.NO_EVIDENCE);

        // initialize concentrations
        ConcentrationBuilder.create(simulation)
                .entity(fructosePhosphate)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(0.1)
                .unit(MOLE_PER_LITRE)
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(aldolase)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(0.2)
                .unit(MOLE_PER_LITRE)
                .build();

        // create reaction using the properties of the enzyme
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(fructosePhosphate)
                .addCatalyst(aldolase)
                .addProduct(glyceraldehyde)
                .addProduct(glyceronePhosphate)
                .michaelisMenten()
                .michaelisConstant(michaelisConstant)
                .turnover(turnoverNumber)
                .build();

        return simulation;
    }

    /**
     * This simulation simulates a diffusion of small molecules in a rectangular gird graph.
     *
     * @param numberOfNodes The number of nodes on one "side" of the rectangle.
     * @param timeStep The size of the time step.
     * @return The ready to go simulation.
     */
    public static Simulation createDiffusionModuleExample(int numberOfNodes, Quantity<Time> timeStep) {

        // setup time step size as given
        UnitRegistry.setTime(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        Environment.setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

        // get required species
        SmallMolecule methanol = ChEBIParserService.parse("CHEBI:17790");
        methanol.setFeature(Diffusivity.class);
        SmallMolecule ethyleneGlycol = ChEBIParserService.parse("CHEBI:30742");
        ethyleneGlycol.setFeature(Diffusivity.class);
        SmallMolecule valine = ChEBIParserService.parse("CHEBI:27266");
        valine.setFeature(Diffusivity.class);
        SmallMolecule sucrose = ChEBIParserService.parse("CHEBI:17992");
        sucrose.setFeature(Diffusivity.class);

        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfNodes, numberOfNodes));

        // initialize species in graph with desired concentration leaving the right "half" empty
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().set(CYTOPLASM, methanol, 1.0);
                node.getConcentrationContainer().set(CYTOPLASM, ethyleneGlycol, 1.0);
                node.getConcentrationContainer().set(CYTOPLASM, valine, 1.0);
                node.getConcentrationContainer().set(CYTOPLASM, sucrose, 1.0);
            }
        }

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);

        Diffusion.inSimulation(simulation)
                .forAllEntities(methanol, ethyleneGlycol, valine, sucrose)
                .forAllSections()
                .build();

        return simulation;
    }

    /**
     * This simulation simulates a multiple reactions involving iodine.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createIodineMultiReactionExample() {

        // setup time step size
        logger.debug("Adjusting time step size ... ");
        UnitRegistry.setTime(Quantities.getQuantity(1.0, MILLI(SECOND)));

        // setup simulation
        Simulation simulation = new Simulation();

        logger.info("Setting up the passive membrane diffusion example ...");
        // get required species
        logger.debug("Importing species ...");
        // Hydron (H+)
        SmallMolecule hydron = ChEBIParserService.parse("CHEBI:15378");
        // Iodide (I-)
        SmallMolecule iodide = ChEBIParserService.parse("CHEBI:16382");
        // Diiodine (I2)
        SmallMolecule diiodine = ChEBIParserService.parse("CHEBI:17606");
        // Water (H2O)
        SmallMolecule water = ChEBIParserService.parse("CHEBI:15377");
        // Hypoiodous acid (HOI)
        SmallMolecule hia = ChEBIParserService.parse("CHEBI:29231");
        // Iodous acid (HIO2)
        SmallMolecule ia = ChEBIParserService.parse("CHEBI:29229");
        // Iodine dioxide (IO2)
        SmallMolecule iodineDioxid = ChEBIParserService.parse("CHEBI:29901");
        // Iodate (IO3-)
        SmallMolecule iodate = ChEBIParserService.parse("CHEBI:29226");

        logger.debug("Setting up example graph ...");
        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        // initialize species in graph with desired concentration
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, hydron, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, iodide, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, diiodine, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, water, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, hia, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, ia, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, iodineDioxid, 0.05);
        graph.getNode(0, 0).getConcentrationContainer().set(CYTOPLASM, iodate, 0.05);


        logger.debug("Composing simulation ... ");

        RateConstant firstRate = RateConstant.create(1.43e3).forward().firstOrder().timeUnit(SECOND).build();

        // first reaction
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(hydron, 2)
                .addSubstrate(iodide)
                .addSubstrate(iodate)
                .addProduct(hia)
                .addProduct(ia)
                .irreversible()
                .rate(firstRate)
                .build();

        RateConstant secondRate = RateConstant.create(2.0e4).forward().firstOrder().timeUnit(SECOND).build();

        // second reaction
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(hydron)
                .addSubstrate(ia)
                .addSubstrate(iodide)
                .addProduct(hia)
                .irreversible()
                .rate(secondRate)
                .build();

        RateConstant thirdForwardRate = RateConstant.create(3.1e4)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant thirdBackwardRate = RateConstant.create(2.2)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // third reaction
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(hia)
                .addSubstrate(iodide)
                .addSubstrate(hydron)
                .addProduct(diiodine)
                .addProduct(water)
                .reversible()
                .forwardReactionRate(thirdForwardRate)
                .backwardReactionRate(thirdBackwardRate)
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }


    public static Simulation createCompartmentTestEnvironment() {
        logger.info("Setting up Compartment Test Example ...");
        // setup rectangular graph with number of nodes
        logger.debug("Setting up example graph ...");
        int numberOfNodes = 50;
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfNodes, numberOfNodes));
        // setup simulation
        logger.debug("Composing simulation ... ");
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);

        return simulation;
    }

}
