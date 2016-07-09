package de.bioforscher.simulation.util;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.deprecated.*;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.GraphAutomaton;
import de.bioforscher.simulation.reactions.EnzymeReaction;
import de.bioforscher.simulation.reactions.EquilibriumReaction;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.logging.Logger;

import static de.bioforscher.units.UnitDictionary.*;

/**
 * A factory class that can be used to create different templates to test
 * certain aspects to the api.
 *
 * @author Christoph Leberecht
 */
public class AutomataFactory {

    private static final Logger log = Logger.getLogger(AutomataFactory.class.getName());

    private static Rectangle defaultBoundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));

    /**
     * This automaton simulates a first-order reaction.
     *
     * @return
     */
    public static GraphAutomaton buildFirstOrderReactionExampleAutomata() {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, defaultBoundingBox));

        ChEBIParserService chebiService = new ChEBIParserService();

        // dinitrogen pentaoxide
        chebiService.setResource("CHEBI:29802");
        Species dpo = chebiService.fetchSpecies();

        // nitrogen dioxide
        chebiService.setResource("CHEBI:33101");
        Species ndo = chebiService.fetchSpecies();

        // dioxigen
        chebiService.setResource("CHEBI:15379");
        Species oxygen = chebiService.fetchSpecies();

        for (BioNode node : graph.getNodes()) {
            node.addEntity(dpo, 0.020);
            node.addEntity(ndo, 0);
            node.addEntity(oxygen, 0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(dpo, 1);
            edge.addPermeability(ndo, 1);
            edge.addPermeability(oxygen, 1);
        }

        // Environment
        EnvironmentFactory.createFirstOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion recurrenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automaton
        GraphAutomaton automaton = new GraphAutomaton(graph, recurrenceDiffusion);

        FirstOrderReaction firstOrderReaction = new FirstOrderReaction.Builder()
                .addSubstrate(dpo)
                .addProduct(ndo)
                .addProduct(oxygen)
                .rateConstant(0.07)
                .build();
        automaton.addReaction(firstOrderReaction, false);

        return automaton;
    }

    /**
     * This automaton simulates a second-order reaction.
     *
     * @return
     */
    public static GraphAutomaton buildSecondOrderReactionTestAutomata() {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildGridGraph(1, 1, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        ChEBIParserService chebiService = new ChEBIParserService();

        // buta-1,3-diene
        chebiService.setResource("CHEBI:39478");
        Species bd = chebiService.fetchSpecies();

        // 1,3,5-octatriene
        chebiService.setResource("CHEBI:77504");
        Species ot = chebiService.fetchSpecies();

        for (BioNode node : graph.getNodes()) {
            node.addEntity(bd, 0.01);
            node.addEntity(ot, 0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(bd, 1);
            edge.addPermeability(ot, 1);
        }

        // Environment
        EnvironmentFactory.createSecondOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion recurrenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomaton automata = new GraphAutomaton(graph, recurrenceDiffusion);

        SecondOrderReaction secondOrderReaction = new SecondOrderReaction.Builder()
                .addSubstrate(bd)
                .addSubstrate(bd) // TODO requires at least two substrates by design - maybe change
                .addProduct(ot)
                .rateConstant(Quantities.getQuantity(0.614, PER_SECOND))
                .addOrder(bd, 1)
                .addOrder(ot, 1)
                .build();
        automata.addReaction(secondOrderReaction, false);

        return automata;
    }

    /**
     * This automaton simulates a equilibrium reaction.
     *
     * @return
     */
    public static GraphAutomaton buildEquilibriumReactionTestAutomata() {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildGridGraph(1, 1, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        // Species
        Species speciesA = new Species.Builder("CHEBI:00001")
                .name("A")
                .molarMass(10.0)
                .build();

        Species speciesB = new Species.Builder("CHEBI:00002")
                .name("B")
                .molarMass(10.0)
                .build();

        for (BioNode node : graph.getNodes()) {
            node.addEntity(speciesA, 1.0);
            node.addEntity(speciesB, 0.0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(speciesA, 1);
            edge.addPermeability(speciesB, 1);
        }

        // Environment
        EnvironmentFactory.createFirstOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomaton automata = new GraphAutomaton(graph, reccurenceDiffusion);

        EquilibriumReaction equilibriumReaction = new EquilibriumReaction.Builder()
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .rateConstantForwards(Quantities.getQuantity(10, PER_SECOND))
                .rateConstantBackwards(Quantities.getQuantity(10, PER_SECOND))
                .build();
        automata.addReaction(equilibriumReaction, false);

        return automata;
    }

    /**
     * This automaton simulates a equilibrium reaction.
     *
     * @return
     */
    public static GraphAutomaton buildEnzymeReactionTestAutomata() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma
        // brucei compared to aldolase from rabbit muscle and Staphylococcus
        // aureus.

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildGridGraph(1, 1, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        // Species
        ChEBIParserService service = new ChEBIParserService();

        service.setResource("CHEBI:18105");
        Species fp = service.fetchSpecies();

        service.setResource("CHEBI:16108");
        Species gp = service.fetchSpecies();

        service.setResource("CHEBI:17378");
        Species ga = service.fetchSpecies();

        // Enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .molarMass(82142.0)
                .criticalSubstrate(fp)
                .michaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE))
                .turnoverNumber(Quantities.getQuantity(76, PER_MINUTE))
                .build();

        // set concentrations
        for (BioNode node : graph.getNodes()) {
            node.addEntity(fp, 0.1);
            node.addEntity(aldolase, 0.2);
            node.addEntity(ga, 0);
            node.addEntity(gp, 0);
        }
        // set permeability
        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(fp, 1);
            edge.addPermeability(aldolase, 1);
            edge.addPermeability(ga, 1);
            edge.addPermeability(gp, 1);
        }

        // Environment
        EnvironmentFactory.createEnzymeReactionTestEnvironment();

        // Diffusion model
        Diffusion recurrenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomaton automaton = new GraphAutomaton(graph, recurrenceDiffusion);

        // Reaction
        EnzymeReaction enzymeReaction = new EnzymeReaction.Builder()
                .enzyme(aldolase)
                .addSubstrate(fp)
                .addProduct(ga)
                .addProduct(gp)
                .build();

        automaton.addReaction(enzymeReaction, false);

        return automaton;
    }

    /**
     * This automaton simulates diffusion of small molecules.
     *
     * @return
     */
    public static GraphAutomaton buildSmallMoleculeDiffusionTestAutomata(int numberOfNodes, Quantity<Time> timeStep) {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        ChEBIParserService service = new ChEBIParserService();

        service.setResource("CHEBI:17790");
        Species methanol = service.fetchSpecies();

        service.setResource("CHEBI:30742");
        Species ethyleneGlycol = service.fetchSpecies();

        service.setResource("CHEBI:27266");
        Species valine = service.fetchSpecies();

        service.setResource("CHEBI:17992");
        Species sucrose = service.fetchSpecies();

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

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();
        EnvironmentalVariables.getInstance().setNodeSpacingToDiameter(EnvironmentalVariables.getInstance().getNodeDistance().multiply(10),
                numberOfNodes);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        return new GraphAutomaton(graph, reccurenceDiffusion);
    }

    /**
     * This automaton simulates diffusion of small molecules.
     *
     * @return
     */
    public static GraphAutomaton buildDiffusionOptimizationTestAutomata(int numberOfNodes, Quantity<Time> timeStep,
                                                                        Species species) {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        // ChEBIParserService service = new ChEBIParserService();

        // service.setResource("CHEBI:17790");
        // Species methanol = service.fetchSpecies();

        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                node.addEntity(species, 1);
            } else {
                node.addEntity(species, 0);
            }
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(species, 1);
        }

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();
        EnvironmentalVariables.getInstance().setNodeSpacingToDiameter(EnvironmentalVariables.getInstance().getNodeDistance().multiply(10),
                numberOfNodes);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);

        // Diffusion model
        Diffusion recurrenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        return new GraphAutomaton(graph, recurrenceDiffusion);
    }

    /**
     * This automaton simulates a first-order reaction.
     *
     * @return
     */
    public static GraphAutomaton buildNthOrderReactionTestAutomata() {

        // Graph
        AutomatonGraph graph = new AutomatonGraph(1, 1);
        BioNode node = new BioNode(0);
        node.setPosition(new Vector2D(10, 10));
        graph.addNode(node);

        ChEBIParserService chebiService = new ChEBIParserService();

        // Hydron (H+)
        chebiService.setResource("CHEBI:15378");
        Species hydron = chebiService.fetchSpecies();

        // Iodide (I-)
        chebiService.setResource("CHEBI:16382");
        Species iodide = chebiService.fetchSpecies();

        // Diiodine (I2)
        chebiService.setResource("CHEBI:17606");
        Species diiodine = chebiService.fetchSpecies();

        // Water (H2O)
        chebiService.setResource("CHEBI:15377");
        Species water = chebiService.fetchSpecies();

        // Hypoiodous acid (HOI)
        chebiService.setResource("CHEBI:29231");
        Species hia = chebiService.fetchSpecies();

        // Iodous acid (HIO2)
        chebiService.setResource("CHEBI:29229");
        Species ia = chebiService.fetchSpecies();

        // Iodine dioxide (IO2)
        chebiService.setResource("CHEBI:29901");
        Species iodineDioxid = chebiService.fetchSpecies();

        // Iodate (IO3-)
        chebiService.setResource("CHEBI:29226");
        Species iodate = chebiService.fetchSpecies();

        graph.getNode(0).addAllEntities(0.05, hydron, iodide, diiodine, water, hia, ia, iodineDioxid, iodate);

        // Environment
        EnvironmentFactory.createNthOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomaton automata = new GraphAutomaton(graph, reccurenceDiffusion);

        // REACTIONS

        // R1
        NthOrderReaction firstReaction = new NthOrderReaction.Builder()
                .addSubstrate(hydron, 2)
                .addSubstrate(iodide)
                .addSubstrate(iodate)
                .addProduct(hia)
                .addProduct(ia)
                .rateConstant(Quantities.getQuantity(1.43e3, PER_SECOND))
                .build();
        automata.addReaction(firstReaction, false);

        // R2
        NthOrderReaction secondReaction = new NthOrderReaction.Builder()
                .addSubstrate(hydron)
                .addSubstrate(ia)
                .addSubstrate(iodide)
                .addProduct(hia)
                .rateConstant(Quantities.getQuantity(2.0e4, PER_SECOND))
                .build();
        automata.addReaction(secondReaction, false);

        // R3
        EquilibriumReaction thirdReaction = new EquilibriumReaction.Builder()
                .addSubstrate(hia)
                .addSubstrate(iodide)
                .addSubstrate(hydron)
                .addProduct(diiodine)
                .addProduct(water)
                .rateConstantForwards(Quantities.getQuantity(3.1e4, PER_SECOND))
                .rateConstantBackwards(Quantities.getQuantity(2.2, PER_SECOND))
                .build();
        automata.addReaction(thirdReaction, false);

        return automata;
    }

    public static GraphAutomaton buildTreeDiffusionTestAutomata(int depth) {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildTreeGraph(depth, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0))));

        ChEBIParserService service = new ChEBIParserService();

        service.setResource("CHEBI:17790");
        Species methanol = service.fetchSpecies();

        graph.getNode(0).addEntity(methanol, 1.0);
        graph.getNode(0).setSource(true);

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(methanol, 1);
        }

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        return new GraphAutomaton(graph, reccurenceDiffusion);
    }

}
