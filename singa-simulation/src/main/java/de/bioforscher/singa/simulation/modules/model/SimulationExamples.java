package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemParserService;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.*;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.mathematics.graphs.grid.GridCoordinateConverter;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.MichaelisMentenReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.singa.simulation.modules.transport.FlipFlopMembraneTransport;
import de.bioforscher.singa.simulation.modules.transport.FreeDiffusion;
import de.bioforscher.singa.simulation.parser.sbml.BioModelsParserService;
import de.bioforscher.singa.simulation.parser.sbml.SBMLParser;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_MINUTE;
import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_SECOND;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

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
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();
        // get required species
        SmallMolecule dinitrogenPentaoxide = ChEBIParserService.parse("CHEBI:29802");
        SmallMolecule nitrogenDioxide = ChEBIParserService.parse("CHEBI:33101");
        SmallMolecule oxygen = ChEBIParserService.parse("CHEBI:15379");

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(dinitrogenPentaoxide, 0.02);
        graph.initializeSpeciesWithConcentration(nitrogenDioxide, 0.0);
        graph.initializeSpeciesWithConcentration(oxygen, 0.0);

        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(dinitrogenPentaoxide, 2)
                .addProduct(nitrogenDioxide, 4)
                .addProduct(oxygen)
                .rateConstant(new RateConstant(Quantities.getQuantity(0.07, PER_SECOND), MANUALLY_ANNOTATED))
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }

    /**
     * This simulation simulates the synthesis of 1,3,5-octatriene (C8H12) from Buta-1,3-diene (C4H6).
     *
     * @return The ready to go simulation.
     */
    public static Simulation createSynthesisReactionExample() {
        // setup time step size
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1.0, SECOND));
        // setup simulation
        Simulation simulation = new Simulation();
        // get required species
        SmallMolecule butadiene = ChEBIParserService.parse("CHEBI:39478");
        SmallMolecule octatriene = ChEBIParserService.parse("CHEBI:77504");

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(butadiene, 0.02);
        graph.initializeSpeciesWithConcentration(octatriene, 0.0);

        // create reaction
        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(butadiene, 2, 2)
                .addProduct(octatriene)
                .rateConstant(new RateConstant(Quantities.getQuantity(0.614, PER_SECOND), MANUALLY_ANNOTATED))
                .setNonElementary()
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
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();

        // set up arbitrary species
        SmallMolecule speciesA = new SmallMolecule.Builder("CHEBI:00001")
                .name("A")
                .build();

        SmallMolecule speciesB = new SmallMolecule.Builder("CHEBI:00002")
                .name("B")
                .build();

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(speciesA, 1.0);
        graph.initializeSpeciesWithConcentration(speciesB, 0.0);

        // create reaction
        EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .forwardsRateConstant(new ForwardsRateConstant(Quantities.getQuantity(10, PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .backwardsRateConstant(new BackwardsRateConstant(Quantities.getQuantity(10, PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }

    /**
     * This simulation simulates a {@link MichaelisMentenReaction}, where D-Fructose 1-phosphate is convertet to
     * glycerone phosphate and D-glyceraldehyde using fructose bisphosphate aldolase. From: Callens, M. et al. (1991).
     * Kinetic properties of fructose bisphosphate aldolase from Trypanosoma brucei compared to aldolase from rabbit
     * muscle and Staphylococcus aureus. Sabio-RK pdbIdentifier: 28851
     *
     * @return The ready to go simulation.
     */
    public static Simulation createMichaelisMentenReactionExample() {
        // setup time step size
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));
        // setup simulation
        Simulation simulation = new Simulation();
        // get required species
        SmallMolecule fructosePhosphate = ChEBIParserService.parse("CHEBI:18105");
        SmallMolecule glyceronePhosphate = ChEBIParserService.parse("CHEBI:16108");
        SmallMolecule glyceraldehyde = ChEBIParserService.parse("CHEBI:17378");

        // setup enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .addSubstrate(fructosePhosphate)
                .assignFeature(new MolarMass(82142, MANUALLY_ANNOTATED))
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()), MANUALLY_ANNOTATED))
                .assignFeature(new TurnoverNumber(Quantities.getQuantity(76, PER_MINUTE), MANUALLY_ANNOTATED))
                .build();

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(fructosePhosphate, 0.1);
        graph.initializeSpeciesWithConcentration(aldolase, 0.2);
        graph.initializeSpeciesWithConcentration(glyceronePhosphate, 0.0);
        graph.initializeSpeciesWithConcentration(glyceraldehyde, 0.0);

        // create reaction using the properties of the enzyme
        MichaelisMentenReaction.inSimulation(simulation)
                .enzyme(aldolase)
                .addSubstrate(fructosePhosphate)
                .addProduct(glyceraldehyde)
                .addProduct(glyceronePhosphate)
                .build();

        // add graph
        simulation.setGraph(graph);

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
        EnvironmentalParameters.setTimeStep(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        EnvironmentalParameters.setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

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
                node.setConcentration(methanol, 1);
                node.setConcentration(ethyleneGlycol, 1);
                node.setConcentration(valine, 1);
                node.setConcentration(sucrose, 1);
            } /* else {
                node.setConcentration(methanol, 0);
                node.setConcentration(ethyleneGlycol, 0);
                node.setConcentration(valine, 0);
                node.setConcentration(sucrose, 0);
            } */
        }

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);

        FreeDiffusion.inSimulation(simulation)
                .forAll(methanol, ethyleneGlycol, valine, sucrose)
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
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));

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
        graph.getNode(0, 0).setConcentrations(0.05, hydron, iodide, diiodine, water, hia, ia, iodineDioxid, iodate);


        logger.debug("Composing simulation ... ");

        // first reaction
        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(hydron, 2)
                .addSubstrate(iodide)
                .addSubstrate(iodate)
                .addProduct(hia)
                .addProduct(ia)
                .rateConstant(new RateConstant(Quantities.getQuantity(1.43e3, PER_SECOND), MANUALLY_ANNOTATED))
                .build();

        // second reaction
        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(hydron)
                .addSubstrate(ia)
                .addSubstrate(iodide)
                .addProduct(hia)
                .rateConstant(new RateConstant(Quantities.getQuantity(2.0e4, PER_SECOND), MANUALLY_ANNOTATED))
                .build();

        // third reaction
        EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(hia)
                .addSubstrate(iodide)
                .addSubstrate(hydron)
                .addProduct(diiodine)
                .addProduct(water)
                .forwardsRateConstant(new ForwardsRateConstant(Quantities.getQuantity(3.1e4, PER_SECOND), MANUALLY_ANNOTATED))
                .backwardsRateConstant(new BackwardsRateConstant(Quantities.getQuantity(2.2, PER_SECOND), MANUALLY_ANNOTATED))
                .build();

        // add graph
        simulation.setGraph(graph);

        return simulation;
    }

    public static Simulation createSimulationFromSBML() {

        // setup time step size
        logger.debug("Adjusting time step size ... ");
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1.0, SECOND));

        // setup simulation
        Simulation simulation = new Simulation();
        // BIOMD0000000023
        // BIOMD0000000064
        // BIOMD0000000184 for ca oscillations

        logger.info("Setting up simulation for model BIOMD0000000184 ...");
        SBMLParser model = BioModelsParserService.parseModelById("BIOMD0000000184");

        logger.debug("Setting up example graph ...");
        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        model.getCompartments().keySet().forEach(graph::addCellSection);

        // initialize species in graph with desired concentration
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        AutomatonNode bioNode = graph.getNodes().iterator().next();
        model.getStartingConcentrations().forEach((entity, value) -> {
            logger.debug("Initialized concentration of {} to {}.", entity.getIdentifier(), value);
            bioNode.setConcentration(entity, Quantities.getQuantity(value, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        });

        // add graph
        simulation.setGraph(graph);
        // add reaction to the reactions used in the simulations
        model.getReactions().forEach(reaction -> reaction.setSimulation(simulation));
        simulation.getModules().addAll(model.getReactions());
        // add, sort and apply assignment rules
        simulation.setAssignmentRules(new ArrayList<>(model.getAssignmentRules()));
        simulation.applyAssignmentRules();

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

    public static Simulation createPassiveMembraneTransportExample() {
        logger.info("Setting up the passive membrane diffusion example ...");
        // get required species
        logger.debug("Importing species ...");
        // setup time step size as given
        logger.debug("Adjusting time step size ... ");
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(100, NANO(SECOND)));
        // setup node distance to diameter
        logger.debug("Adjusting spatial step size ... ");
        EnvironmentalParameters.setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), 11);
        // all species
        Set<ChemicalEntity> chemicalEntities = new HashSet<>();
        // Domperidone
        SmallMolecule domperidone = PubChemParserService.parse("CID:3151");
        domperidone.setFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED));
        domperidone.setFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED));
        domperidone.setFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED));
        chemicalEntities.add(domperidone);
        // Loperamide
        SmallMolecule loperamide = PubChemParserService.parse("CID:3955");
        loperamide.setFeature(new MembraneEntry(8.59e8, MANUALLY_ANNOTATED));
        loperamide.setFeature(new MembraneExit(1.81e3, MANUALLY_ANNOTATED));
        loperamide.setFeature(new MembraneFlipFlop(6.71e5, MANUALLY_ANNOTATED));
        chemicalEntities.add(loperamide);
        // Propranolol
        SmallMolecule propranolol = PubChemParserService.parse("CID:4946");
        propranolol.setFeature(new MembraneEntry(1.27e9, MANUALLY_ANNOTATED));
        propranolol.setFeature(new MembraneExit(3.09e4, MANUALLY_ANNOTATED));
        propranolol.setFeature(new MembraneFlipFlop(4.75e6, MANUALLY_ANNOTATED));
        chemicalEntities.add(propranolol);
        // Desipramine
        SmallMolecule desipramine = PubChemParserService.parse("CID:2995");
        desipramine.setFeature(new MembraneEntry(2.13e9, MANUALLY_ANNOTATED));
        desipramine.setFeature(new MembraneExit(4.86e4, MANUALLY_ANNOTATED));
        desipramine.setFeature(new MembraneFlipFlop(1.09e7, MANUALLY_ANNOTATED));
        chemicalEntities.add(desipramine);

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");

        logger.debug("Setting up example graph ...");
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(11, 11);
        AutomatonGraphs.splitRectangularGraphWithMembrane(graph, right, left, false);

        // set concentrations
        // only 5 left most nodes
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                for (ChemicalEntity species : chemicalEntities) {
                    node.setConcentration(species, 1.0);
                }
            } else {
                for (ChemicalEntity species : chemicalEntities) {
                    node.setConcentration(species, 0.0);
                }
            }
        }

        // setup simulation
        logger.debug("Composing simulation ... ");
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add transmembrane transport
        simulation.getModules().add(new FlipFlopMembraneTransport(simulation));

        FreeDiffusion.inSimulation(simulation)
                .forAll(chemicalEntities)
                .build();

        return simulation;
    }

    public static Simulation createDiffusionAndMembraneTransportExample() {
        SmallMolecule domperidone = new SmallMolecule.Builder("CHEBI:31515")
                .name("domperidone")
                .assignFeature(Diffusivity.class)
                .assignFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();
        GridCoordinateConverter gcc = new GridCoordinateConverter(30, 20);
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(20, 30));
        // create compartments and membrane
        EnclosedCompartment inner = new EnclosedCompartment("I", "Inner");
        EnclosedCompartment outer = new EnclosedCompartment("O", "Outer");
        Membrane membrane = Membrane.forCompartment(inner);
        // initialize species in graph with desired concentration
        for (AutomatonNode node : graph.getNodes()) {
            RectangularCoordinate coordinate = node.getIdentifier();
            if ((coordinate.getColumn() == 2 && coordinate.getRow() > 2 && coordinate.getRow() < 17) ||
                    (coordinate.getColumn() == 27 && coordinate.getRow() > 2 && coordinate.getRow() < 17) ||
                    (coordinate.getRow() == 2 && coordinate.getColumn() > 1 && coordinate.getColumn() < 28) ||
                    (coordinate.getRow() == 17 && coordinate.getColumn() > 1 && coordinate.getColumn() < 28)) {
                // setup membrane
                node.setCellSection(membrane);
                node.setConcentrationContainer(new MembraneContainer(outer, inner, membrane));
                node.setAvailableConcentration(domperidone, outer, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
            } else if (coordinate.getColumn() > 2 && coordinate.getRow() > 2 && coordinate.getColumn() < 27 && coordinate.getRow() < 17) {
                node.setCellSection(inner);
                node.setConcentration(domperidone, 0.0);
            } else {
                node.setCellSection(outer);
                node.setConcentration(domperidone, 1.0);
            }
        }

        simulation.setGraph(graph);

        FreeDiffusion.inSimulation(simulation)
                .onlyFor(domperidone)
                .build();

        FlipFlopMembraneTransport membraneTransport = new FlipFlopMembraneTransport(simulation);
        simulation.getModules().add(membraneTransport);

        return simulation;
    }


}
