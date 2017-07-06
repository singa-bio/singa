package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioEdge;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.singa.simulation.modules.membranetransport.PassiveMembraneTransport;
import de.bioforscher.singa.simulation.modules.reactions.implementations.BiochemicalReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reactions;
import de.bioforscher.singa.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.singa.simulation.parser.sbml.BioModelsParserService;
import de.bioforscher.singa.simulation.parser.sbml.SBMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.*;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * A factory class that can be used to create different examples to test and explore certain aspects to the api.
 *
 * @author cl
 */
public class SimulationExamples {

    private static final Logger logger = LoggerFactory.getLogger(SimulationExamples.class);

    private static Rectangle defaultBoundingBox = new Rectangle(new Vector2D(0, 600), new Vector2D(600, 0));

    /**
     * This simulation simulates the thermal decomposition of dinitrogen pentaoxide.
     * From: Brauer, G. (2012). Handbook of preparative inorganic chemistry, volume 2. Elsevier. 489–491.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createDecompositionReactionExample() {

        // get required species
        Species dinitrogenPentaoxide = ChEBIParserService.parse("CHEBI:29802");
        Species nitrogenDioxide = ChEBIParserService.parse("CHEBI:33101");
        Species oxygen = ChEBIParserService.parse("CHEBI:15379");

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(dinitrogenPentaoxide, 0.02);
        graph.initializeSpeciesWithConcentration(nitrogenDioxide, 0.0);
        graph.initializeSpeciesWithConcentration(oxygen, 0.0);

        // setup time step size
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));

        // create reactions module
        Reactions reactions = new Reactions();

        // create reaction
        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.07, UnitProvider.PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dinitrogenPentaoxide, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(nitrogenDioxide, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));

        // add reaction to the reactions used in the simulation
        reactions.getReactions().add(reaction);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    /**
     * This simulation simulates the synthesis of 1,3,5-octatriene (C8H12) from Buta-1,3-diene (C4H6).
     *
     * @return The ready to go simulation.
     */
    public static Simulation createSynthesisReactionExample() {

        // get required species
        Species butadiene = ChEBIParserService.parse("CHEBI:39478");
        Species octatriene = ChEBIParserService.parse("CHEBI:77504");

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(butadiene, 0.02);
        graph.initializeSpeciesWithConcentration(octatriene, 0.0);

        // setup time step size
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, SECOND));

        // create reactions module
        Reactions reactions = new Reactions();

        // create reaction
        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.614, PER_SECOND));
        reaction.setElementary(false);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(butadiene, ReactantRole.DECREASING, 2, 2),
                new StoichiometricReactant(octatriene, ReactantRole.INCREASING)
        ));

        // add reaction to the reactions used in the simulation
        reactions.getReactions().add(reaction);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    /**
     * This simulation simulates a equilibrium reaction.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createEquilibriumReactionExample() {

        // set up arbitrary species
        Species speciesA = new Species.Builder("CHEBI:00001")
                .name("A")
                .build();

        Species speciesB = new Species.Builder("CHEBI:00002")
                .name("B")
                .build();

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(speciesA, 1.0);
        graph.initializeSpeciesWithConcentration(speciesB, 0.0);

        // setup time step size
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));

        // create reactions module
        Reactions reactions = new Reactions();

        // create reaction
        EquilibriumReaction reaction = new EquilibriumReaction(Quantities.getQuantity(10, PER_SECOND),
                Quantities.getQuantity(10, PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(speciesA, ReactantRole.DECREASING),
                new StoichiometricReactant(speciesB, ReactantRole.INCREASING)
        ));

        // add reaction to the reactions used in the simulation
        reactions.getReactions().add(reaction);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    /**
     * This simulation simulates a {@link BiochemicalReaction}, where D-Fructose 1-phosphate is convertet to glycerone
     * phosphate and D-glyceraldehyde using fructose bisphosphate aldolase.
     * From: Callens, M. et al. (1991). Kinetic properties of fructose bisphosphate aldolase from Trypanosoma brucei
     * compared to aldolase from rabbit muscle and Staphylococcus aureus.
     * Sabio-RK pdbIdentifier: 28851
     *
     * @return The ready to go simulation.
     */
    public static Simulation createMichaelisMentenReactionExample() {

        // get required species
        Species fructosePhosphate = ChEBIParserService.parse("CHEBI:18105");
        Species glyceronePhosphate = ChEBIParserService.parse("CHEBI:16108");
        Species glyceraldehyde = ChEBIParserService.parse("CHEBI:17378");

        // setup enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .assignFeature(new MolarMass(82142, MANUALLY_ANNOTATED))
                .addSubstrate(fructosePhosphate)
                .michaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE))
                .turnoverNumber(Quantities.getQuantity(76, PER_MINUTE))
                .build();

        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(fructosePhosphate, 0.1);
        graph.initializeSpeciesWithConcentration(aldolase, 0.2);
        graph.initializeSpeciesWithConcentration(glyceronePhosphate, 0.0);
        graph.initializeSpeciesWithConcentration(glyceraldehyde, 0.0);

        // setup time step size
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));

        // create reactions module
        Reactions reactions = new Reactions();

        // create reaction using the properties of the enzyme
        BiochemicalReaction reaction = new BiochemicalReaction(aldolase);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(fructosePhosphate, ReactantRole.DECREASING),
                new StoichiometricReactant(glyceronePhosphate, ReactantRole.INCREASING),
                new StoichiometricReactant(glyceraldehyde, ReactantRole.INCREASING)
        ));

        // add reaction to the reactions used in the simulation
        reactions.getReactions().add(reaction);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    /**
     * This simulation simulates a diffusion of small molecules in a rectangular gird graph.
     *
     * @param numberOfNodes The number of nodes on one "side" of the rectangle.
     * @param timeStep      The size of the time step.
     * @return The ready to go simulation.
     */
    public static Simulation createDiffusionModuleExample(int numberOfNodes, Quantity<Time> timeStep) {

        // get required species
        Species methanol = ChEBIParserService.parse("CHEBI:17790");
        Species ethyleneGlycol = ChEBIParserService.parse("CHEBI:30742");
        Species valine = ChEBIParserService.parse("CHEBI:27266");
        Species sucrose = ChEBIParserService.parse("CHEBI:17992");

        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, defaultBoundingBox, false));

        // initialize species in graph with desired concentration leaving the right "half" empty
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                node.setConcentration(methanol, 1);
                node.setConcentration(ethyleneGlycol, 1);
                node.setConcentration(valine, 1);
                node.setConcentration(sucrose, 1);
            } else {
                node.setConcentration(methanol, 0);
                node.setConcentration(ethyleneGlycol, 0);
                node.setConcentration(valine, 0);
                node.setConcentration(sucrose, 0);
            }
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(methanol, 1);
            edge.addPermeability(ethyleneGlycol, 1);
            edge.addPermeability(valine, 1);
            edge.addPermeability(sucrose, 1);
        }

        // setup time step size as given
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(
                Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add diffusion module
        simulation.getModules().add(new FreeDiffusion());
        // add desired species to the simulation for easy access
        simulation.getChemicalEntities().addAll(Arrays.asList(methanol, ethyleneGlycol, valine, sucrose));

        return simulation;
    }

    /**
     * This simulation simulates a multiple reactions involving iodine.
     *
     * @return The ready to go simulation.
     */
    public static Simulation createIodineMultiReactionExample() {
        logger.info("Setting up the passive membrane diffusion example ...");
        // get required species
        logger.debug("Importing species ...");
        // Hydron (H+)
        Species hydron = ChEBIParserService.parse("CHEBI:15378");
        // Iodide (I-)
        Species iodide = ChEBIParserService.parse("CHEBI:16382");
        // Diiodine (I2)
        Species diiodine = ChEBIParserService.parse("CHEBI:17606");
        // Water (H2O)
        Species water = ChEBIParserService.parse("CHEBI:15377");
        // Hypoiodous acid (HOI)
        Species hia = ChEBIParserService.parse("CHEBI:29231");
        // Iodous acid (HIO2)
        Species ia = ChEBIParserService.parse("CHEBI:29229");
        // Iodine dioxide (IO2)
        Species iodineDioxid = ChEBIParserService.parse("CHEBI:29901");
        // Iodate (IO3-)
        Species iodate = ChEBIParserService.parse("CHEBI:29226");

        logger.debug("Setting up example graph ...");
        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));
        // initialize species in graph with desired concentration
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        graph.getNode(0).setConcentrations(0.05, hydron, iodide, diiodine, water, hia, ia, iodineDioxid, iodate);

        // setup time step size
        logger.debug("Adjusting time step size ... ");
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));

        logger.debug("Composing simulation ... ");
        // create reactions module
        Reactions reactions = new Reactions();

        // create first reaction
        NthOrderReaction firstReaction = new NthOrderReaction(Quantities.getQuantity(1.43e3, PER_SECOND));
        firstReaction.setElementary(true);
        firstReaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(hydron, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(iodide, ReactantRole.DECREASING),
                new StoichiometricReactant(iodate, ReactantRole.DECREASING),
                new StoichiometricReactant(hia, ReactantRole.INCREASING),
                new StoichiometricReactant(ia, ReactantRole.INCREASING)
        ));

        // create second reaction
        NthOrderReaction secondReaction = new NthOrderReaction(Quantities.getQuantity(2.0e4, PER_SECOND));
        secondReaction.setElementary(true);
        secondReaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(hydron, ReactantRole.DECREASING),
                new StoichiometricReactant(ia, ReactantRole.DECREASING),
                new StoichiometricReactant(iodide, ReactantRole.DECREASING),
                new StoichiometricReactant(hia, ReactantRole.INCREASING)
        ));

        // create second reaction
        EquilibriumReaction thirdReaction = new EquilibriumReaction(Quantities.getQuantity(3.1e4, PER_SECOND),
                Quantities.getQuantity(2.2, PER_SECOND));
        thirdReaction.setElementary(true);
        thirdReaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(hia, ReactantRole.DECREASING),
                new StoichiometricReactant(iodide, ReactantRole.DECREASING),
                new StoichiometricReactant(hydron, ReactantRole.DECREASING),
                new StoichiometricReactant(diiodine, ReactantRole.INCREASING),
                new StoichiometricReactant(water, ReactantRole.INCREASING)
        ));

        // add reaction to the reactions used in the simulation
        reactions.getReactions().addAll(Arrays.asList(firstReaction, secondReaction, thirdReaction));

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    public static Simulation createSimulationFromSBML() {

        // BIOMD0000000023
        // BIOMD0000000064
        // BIOMD0000000184 for ca oscillations

        logger.info("Setting up simulation for model BIOMD0000000184 ...");
        SBMLParser model = BioModelsParserService.parseModelById("BIOMD0000000184");

        logger.debug("Setting up example graph ...");
        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        model.getCompartments().keySet().forEach(graph::addSection);

        // initialize species in graph with desired concentration
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        BioNode bioNode = graph.getNodes().iterator().next();
        model.getStartingConcentrations().forEach((entity, value) -> {
            logger.debug("Initialized concentration of {} to {}.", entity.getIdentifier(), value);
            bioNode.setConcentration(entity, value);
        });

        // setup time step size
        logger.debug("Adjusting time step size ... ");
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, SECOND));

        // create reactions module
        Reactions reactions = new Reactions();

        // add reaction to the reactions used in the simulations
        reactions.getReactions().addAll(model.getReactions());

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add the reactions module
        simulation.getModules().add(reactions);
        // add, sort and apply assignment rules
        simulation.setAssignmentRules(new ArrayList<>(model.getAssignmentRules()));
        simulation.applyAssignmentRules();
        // add all referenced species to the simulation for easy access
        simulation.getChemicalEntities().addAll(simulation.collectAllReferencedEntities());

        return simulation;
    }

    public static Simulation createCompartmentTestEnvironment() {

        logger.info("Setting up Compartment Test Example ...");
        // setup rectangular graph with number of nodes
        logger.debug("Setting up example graph ...");
        int numberOfNodes = 50;
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, defaultBoundingBox, false));
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

        // all species
        Set<Species> allSpecies = new HashSet<>();
        // Domperidone
        Species domperidone = PubChemParserService.parse("CID:3151");
        domperidone.setFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED));
        domperidone.setFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED));
        domperidone.setFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED));
        allSpecies.add(domperidone);
        // Loperamide
        Species loperamide = PubChemParserService.parse("CID:3955");
        loperamide.setFeature(new MembraneEntry(8.59e8, MANUALLY_ANNOTATED));
        loperamide.setFeature(new MembraneExit(1.81e3, MANUALLY_ANNOTATED));
        loperamide.setFeature(new MembraneFlipFlop(6.71e5, MANUALLY_ANNOTATED));
        allSpecies.add(loperamide);
        // Propranolol
        Species propranolol = PubChemParserService.parse("CID:4946");
        propranolol.setFeature(new MembraneEntry(1.27e9, MANUALLY_ANNOTATED));
        propranolol.setFeature(new MembraneExit(3.09e4, MANUALLY_ANNOTATED));
        propranolol.setFeature(new MembraneFlipFlop(4.75e6, MANUALLY_ANNOTATED));
        allSpecies.add(propranolol);
        // Desipramine
        Species desipramine = PubChemParserService.parse("CID:2995");
        desipramine.setFeature(new MembraneEntry(2.13e9, MANUALLY_ANNOTATED));
        desipramine.setFeature(new MembraneExit(4.86e4, MANUALLY_ANNOTATED));
        desipramine.setFeature(new MembraneFlipFlop(1.09e7, MANUALLY_ANNOTATED));
        allSpecies.add(desipramine);

        // setup rectangular graph with number of nodes
        logger.debug("Setting up example graph ...");
        int numberOfNodes = 11;
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, defaultBoundingBox, false));

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = new Membrane("LC-M", "Left-Membrane", left);

        // compartments have to be set before concentrations can be set
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < (numberOfNodes / 2)) {
                // FIXME currently cell sections are assigned in both directions
                node.setCellSection(left);
                left.addNode(node);
            } else if (node.getIdentifier() % numberOfNodes == (numberOfNodes / 2)) {
                node.setState(NodeState.MEMBRANE);
                node.setCellSection(membrane);
                membrane.addNode(node);
            } else {
                node.setCellSection(right);
                right.addNode(node);
            }
        }

        // setup compartments
        graph.addSection(left);
        graph.addSection(right);
        graph.addSection(membrane);
        membrane.initializeNodes(graph);

        // set concentrations
        // only 4 left most nodes
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < 4) {
                for (Species species: allSpecies) {
                    node.setConcentration(species, 1.0);
                }
            } else {
                for (Species species: allSpecies) {
                    node.setConcentration(species, 0.0);
                }
            }
        }

        // setup time step size as given
        logger.debug("Adjusting time step size ... ");
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(100, NANO(SECOND)));
        // setup node distance to diameter
        logger.debug("Adjusting spatial step size ... ");
        EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(
                Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

        // setup simulation
        logger.debug("Composing simulation ... ");
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add diffusion module
        simulation.getModules().add(new FreeDiffusion());
        // add transmembrane transport
        simulation.getModules().add(new PassiveMembraneTransport());
        // add desired species to the simulation for easy access
        simulation.getChemicalEntities().addAll(allSpecies);
        return simulation;
    }


}
