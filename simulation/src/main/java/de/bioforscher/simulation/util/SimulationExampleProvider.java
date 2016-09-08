package de.bioforscher.simulation.util;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.simulation.modules.model.Simulation;
import de.bioforscher.simulation.modules.reactions.implementations.BiochemicalReaction;
import de.bioforscher.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.simulation.modules.reactions.model.Reactions;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.units.UnitDictionary;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.Arrays;
import java.util.logging.Logger;

import static de.bioforscher.units.UnitDictionary.*;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * A factory class that can be used to create different examples to test and explore certain aspects to the api.
 *
 * @author Christoph Leberecht
 */
public class SimulationExampleProvider {

    private static final Logger log = Logger.getLogger(SimulationExampleProvider.class.getName());

    private static Rectangle defaultBoundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));

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
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(dinitrogenPentaoxide, 0.02);
        graph.initializeSpeciesWithConcentration(nitrogenDioxide, 0.0);
        graph.initializeSpeciesWithConcentration(oxygen, 0.0);

        // setup time step size
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));

        // create reactions module
        Reactions reactions = new Reactions();

        // create reaction
        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.07, UnitDictionary.PER_SECOND));
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
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(butadiene, 0.02);
        graph.initializeSpeciesWithConcentration(octatriene, 0.0);

        // setup time step size
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, SECOND));

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
                .molarMass(10.0)
                .build();

        Species speciesB = new Species.Builder("CHEBI:00002")
                .name("B")
                .molarMass(10.0)
                .build();

        // setup graph with a single node
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(speciesA, 1.0);
        graph.initializeSpeciesWithConcentration(speciesB, 0.0);

        // setup time step size
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(10.0, MILLI(SECOND)));

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
     * Sabio-RK identifier: 28851
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
                .molarMass(82142.0)
                .criticalSubstrate(fructosePhosphate)
                .michaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE))
                .turnoverNumber(Quantities.getQuantity(76, PER_MINUTE))
                .build();

        // setup graph with a single node
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        // initialize species in graph with desired concentration
        graph.initializeSpeciesWithConcentration(fructosePhosphate, 0.1);
        graph.initializeSpeciesWithConcentration(aldolase, 0.2);
        graph.initializeSpeciesWithConcentration(glyceronePhosphate, 0.0);
        graph.initializeSpeciesWithConcentration(glyceraldehyde, 0.0);

        // setup time step size
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));

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
     * @param timeStep The size of the time step.
     * @return The ready to go simulation.
     */
    public static Simulation createDiffusionModuleExample(int numberOfNodes, Quantity<Time> timeStep) {

        // get required species
        Species methanol = ChEBIParserService.parse("CHEBI:17790");
        Species ethyleneGlycol = ChEBIParserService.parse("CHEBI:30742");
        Species valine = ChEBIParserService.parse("CHEBI:27266");
        Species sucrose = ChEBIParserService.parse("CHEBI:17992");

        // setup rectangular graph with number of nodes
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, defaultBoundingBox, false));

        // initialize species in graph with desired concentration leaving the right "half" empty
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                node.addEntity(methanol, 1);
                node.addEntity(ethyleneGlycol, 1);
                node.addEntity(valine, 1);
                node.addEntity(sucrose, 1);
            } else {
                node.addEntity(methanol, 0);
                node.addEntity(ethyleneGlycol, 0);
                node.addEntity(valine, 0);
                node.addEntity(sucrose, 0);
            }
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(methanol, 1);
            edge.addPermeability(ethyleneGlycol, 1);
            edge.addPermeability(valine, 1);
            edge.addPermeability(sucrose, 1);
        }

        // setup time step size as given
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        EnvironmentalVariables.getInstance().setNodeSpacingToDiameter(
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

        // setup graph with a single node
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));
        // initialize species in graph with desired concentration
        graph.getNode(0).addAllEntities(0.05, hydron, iodide, diiodine, water, hia, ia, iodineDioxid, iodate);

        // setup time step size
        EnvironmentalVariables.getInstance().setTimeStep(Quantities.getQuantity(5.0, MILLI(SECOND)));

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

}
