package de.bioforscher.simulation.util;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.diffusion.Diffusion;
import de.bioforscher.simulation.diffusion.RecurrenceDiffusion;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.GraphAutomata;
import de.bioforscher.simulation.reactions.*;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;
import static de.bioforscher.units.UnitDictionary.PER_SECOND;
import static tec.units.ri.unit.Units.MINUTE;
import static tec.units.ri.unit.Units.ONE;

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
    public static GraphAutomata buildFirstOrderReactionExampleAutomata() {

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
            edge.addSpeciesPermeability(dpo, 1);
            edge.addSpeciesPermeability(ndo, 1);
            edge.addSpeciesPermeability(oxygen, 1);
        }

        // Environment
        EnvironmentFactory.createFirstOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        // Rate constant
        Quantity<ReactionRate> rateConstant = Quantities.getQuantity(0.070, PER_SECOND);

        // Substrates
        ArrayList<Species> substrates = new ArrayList<Species>();
        substrates.add(dpo);

        // Products
        ArrayList<Species> products = new ArrayList<Species>();
        products.add(ndo);
        products.add(oxygen);

        // Stoichiometric Coefficients
        Map<Species, Integer> stoichiometricCoefficients = new HashMap<Species, Integer>();
        stoichiometricCoefficients.put(dpo, 1);
        stoichiometricCoefficients.put(ndo, 1);
        stoichiometricCoefficients.put(oxygen, 1);

        // Setup reaction
        FirstOrderReaction reaction = new FirstOrderReaction(substrates, products, stoichiometricCoefficients,
                rateConstant);
        automata.addReaction(reaction, false);

        return automata;
    }

    /**
     * This automaton simulates a first2DVector-order reaction.
     *
     * @return
     */
    public static GraphAutomata buildSecondOrderReactionTestAutomata() {

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
            edge.addSpeciesPermeability(bd, 1);
            edge.addSpeciesPermeability(ot, 1);
        }

        // Environment
        EnvironmentFactory.createSecondOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        // Rate constant
        Quantity<ReactionRate> rateConstant = Quantities.getQuantity(0.0614, PER_SECOND);

        // Substrates
        ArrayList<Species> substrates = new ArrayList<Species>();
        substrates.add(bd);
        substrates.add(bd);

        // Products
        ArrayList<Species> products = new ArrayList<Species>();
        products.add(ot);

        // Stoichiometric Coefficients
        Map<Species, Integer> stoichiometricCoefficients = new HashMap<Species, Integer>();
        stoichiometricCoefficients.put(bd, 1);
        stoichiometricCoefficients.put(ot, 1);

        // Orders
        Map<Species, Double> orders = new HashMap<Species, Double>();
        orders.put(bd, 1.0);
        orders.put(ot, 1.0);

        // Setup reaction
        SecondOrderReaction reaction = new SecondOrderReaction(substrates, products, orders, stoichiometricCoefficients,
                rateConstant);
        automata.addReaction(reaction, false);

        return automata;
    }

    /**
     * This automaton simulates a equilibrium reaction.
     *
     * @return
     */
    public static GraphAutomata buildEquilibriumReactionTestAutomata() {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildGridGraph(1, 1, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0)), false));

        // Species
        Species speciesA = new Species("A", "A", 10.0);
        Species speciesB = new Species("B", "B", 10.0);

        for (BioNode node : graph.getNodes()) {
            node.addEntity(speciesA, 1.0);
            node.addEntity(speciesB, 0.0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addSpeciesPermeability(speciesA, 1);
            edge.addSpeciesPermeability(speciesB, 1);
        }

        // Environment
        EnvironmentFactory.createFirstOrderReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        // Rate constant
        Quantity<ReactionRate> rateForwards = Quantities.getQuantity(10, PER_SECOND);

        // Rate constant
        Quantity<ReactionRate> rateBackwards = Quantities.getQuantity(10, PER_SECOND);

        // Substrates
        ArrayList<Species> substrates = new ArrayList<Species>();
        substrates.add(speciesA);

        // Products
        ArrayList<Species> products = new ArrayList<Species>();
        products.add(speciesB);

        Map<Species, Integer> stoichiometricCoefficients = new HashMap<Species, Integer>();
        stoichiometricCoefficients.put(speciesA, 1);
        stoichiometricCoefficients.put(speciesB, 1);

        EquilibriumReaction reaction = new EquilibriumReaction(substrates, products, stoichiometricCoefficients,
                rateForwards, rateBackwards);
        automata.addReaction(reaction, false);

        return automata;
    }

    /**
     * This automaton simulates a equilibrium reaction.
     *
     * @return
     */
    public static GraphAutomata buildEnzymeReactionTestAutomata() {
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
        Enzyme enzyme = new Enzyme("Fructose-bisphosphate aldolase");
        enzyme.setMolarMass(41071 * 2.0);
        enzyme.setCriticalSubstrate(fp);
        enzyme.setkM(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE));
        enzyme.setkCat(Quantities.getQuantity(76, new ProductUnit<ReactionRate>(ONE.divide(MINUTE))));

        // set concentrations
        for (BioNode node : graph.getNodes()) {
            node.addEntity(fp, 0.1);
            node.addEntity(enzyme, 0.2);
            node.addEntity(ga, 0);
            node.addEntity(gp, 0);
        }
        // set permeability
        for (BioEdge edge : graph.getEdges()) {
            edge.addSpeciesPermeability(fp, 1);
            edge.addSpeciesPermeability(enzyme, 1);
            edge.addSpeciesPermeability(ga, 1);
            edge.addSpeciesPermeability(gp, 1);
        }

        // Environment
        EnvironmentFactory.createEnzymeReactionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        // Reaction
        ArrayList<Species> substrates = new ArrayList<Species>();
        substrates.add(fp);

        ArrayList<Species> products = new ArrayList<Species>();
        products.add(ga);
        products.add(gp);

        Map<Species, Integer> stoichiometricCoefficients = new HashMap<Species, Integer>();
        stoichiometricCoefficients.put(enzyme, 1);
        stoichiometricCoefficients.put(ga, 1);
        stoichiometricCoefficients.put(gp, 1);
        stoichiometricCoefficients.put(fp, 1);

        EnzymeReaction reaction = new EnzymeReaction(substrates, products, stoichiometricCoefficients, enzyme);
        automata.addReaction(reaction, false);

        return automata;
    }

    /**
     * This automaton simulates diffusion of small molecules.
     *
     * @return
     */
    public static GraphAutomata buildSmallMoleculeDiffusionTestAutomata(int numberOfNodes, Quantity<Time> timeStep) {

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
            edge.addSpeciesPermeability(methanol, 1);
            edge.addSpeciesPermeability(ethyleneGlycol, 1);
            edge.addSpeciesPermeability(valine, 1);
            edge.addSpeciesPermeability(sucrose, 1);
        }

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();
        BioGraphUtilities.setNodeSpacingToDiameter(EnvironmentalVariables.getInstance().getNodeDistance().multiply(10),
                numberOfNodes);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        return automata;
    }

    /**
     * This automaton simulates diffusion of small molecules.
     *
     * @return
     */
    public static GraphAutomata buildDiffusionOptimizationTestAutomata(int numberOfNodes, Quantity<Time> timeStep,
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
            edge.addSpeciesPermeability(species, 1);
        }

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();
        BioGraphUtilities.setNodeSpacingToDiameter(EnvironmentalVariables.getInstance().getNodeDistance().multiply(10),
                numberOfNodes);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        return automata;
    }

    /**
     * This automaton simulates a first-order reaction.
     *
     * @return
     */
    public static GraphAutomata buildNthOrderReactionTestAutomata() {

        // Graph
        AutomatonGraph graph = new AutomatonGraph(1, 1);
        graph.addNode(new BioNode(0));

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
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        // REACTIONS

        // R1
        // Substrates
        ArrayList<Species> substrates1 = new ArrayList<Species>();
        substrates1.add(hydron);
        substrates1.add(iodide);
        substrates1.add(iodate);
        // Products
        ArrayList<Species> products1 = new ArrayList<Species>();
        products1.add(hia);
        products1.add(ia);
        // Stoichiometric Coefficients
        Map<Species, Integer> stoichiometricCoefficients1 = new HashMap<Species, Integer>();
        stoichiometricCoefficients1.put(hydron, 2);
        stoichiometricCoefficients1.put(iodide, 1);
        stoichiometricCoefficients1.put(iodate, 1);
        stoichiometricCoefficients1.put(hia, 1);
        stoichiometricCoefficients1.put(ia, 1);
        // Rate constant lit 1.43e3
        Quantity<ReactionRate> rateConstant1 = Quantities.getQuantity(1.43e3, PER_SECOND);
        // Setup reaction
        NthOrderReaction reaction1 = new NthOrderReaction(substrates1, products1, stoichiometricCoefficients1,
                rateConstant1);
        automata.addReaction(reaction1, false);

        // R2
        // Substrates
        ArrayList<Species> substrates2 = new ArrayList<Species>();
        substrates2.add(hydron);
        substrates2.add(ia);
        substrates2.add(iodide);
        // Products
        ArrayList<Species> products2 = new ArrayList<Species>();
        products2.add(hia);
        // Stoichiometric Coefficients
        Map<Species, Integer> stoichiometricCoefficients2 = new HashMap<Species, Integer>();
        stoichiometricCoefficients2.put(hydron, 1);
        stoichiometricCoefficients2.put(ia, 1);
        stoichiometricCoefficients2.put(iodide, 1);
        stoichiometricCoefficients2.put(hia, 1);
        // Rate constant lit 2e10
        Quantity<ReactionRate> rateConstant2 = Quantities.getQuantity(2e4, PER_SECOND);
        // Setup reaction
        NthOrderReaction reaction2 = new NthOrderReaction(substrates2, products2, stoichiometricCoefficients2,
                rateConstant2);
        automata.addReaction(reaction2, false);

        // R3
        // Substrates
        ArrayList<Species> substrates3 = new ArrayList<Species>();
        substrates3.add(hia);
        substrates3.add(iodide);
        substrates3.add(hydron);
        // Products
        ArrayList<Species> products3 = new ArrayList<Species>();
        products3.add(diiodine);
        products3.add(water);
        // Stoichiometric Coefficients
        Map<Species, Integer> stoichiometricCoefficients3 = new HashMap<Species, Integer>();
        stoichiometricCoefficients3.put(hia, 1);
        stoichiometricCoefficients3.put(iodide, 1);
        stoichiometricCoefficients3.put(hydron, 1);
        stoichiometricCoefficients3.put(diiodine, 1);
        stoichiometricCoefficients3.put(water, 1);
        // Rate constant lit kf 3.1 10^10 kb 2.2
        Quantity<ReactionRate> rateConstant3f = Quantities.getQuantity(3.1e4, PER_SECOND);
        Quantity<ReactionRate> rateConstant3b = Quantities.getQuantity(2.2, PER_SECOND);
        // Setup reaction
        EquilibriumReaction reaction3 = new EquilibriumReaction(substrates3, products3, stoichiometricCoefficients3,
                rateConstant3f, rateConstant3b);
        automata.addReaction(reaction3, false);

        return automata;
    }

    public static GraphAutomata buildTreeDiffusionTestAutomata(int depth) {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildTreeGraph(depth, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0))));

        ChEBIParserService service = new ChEBIParserService();

        service.setResource("CHEBI:17790");
        Species methanol = service.fetchSpecies();

        graph.getNode(0).addEntity(methanol, 1.0);
        graph.getNode(0).setSource(true);

        for (BioEdge edge : graph.getEdges()) {
            edge.addSpeciesPermeability(methanol, 1);
        }

        // Environment
        EnvironmentFactory.createSmallDiffusionTestEnvironment();

        // Diffusion model
        Diffusion reccurenceDiffusion = new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph));

        // Automata
        GraphAutomata automata = new GraphAutomata(graph, reccurenceDiffusion);

        return automata;
    }

}
