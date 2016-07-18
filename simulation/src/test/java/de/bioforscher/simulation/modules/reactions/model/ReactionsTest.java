package de.bioforscher.simulation.modules.reactions.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.BiochemicalReaction;
import de.bioforscher.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.simulation.util.EnvironmentFactory;
import de.bioforscher.units.UnitDictionary;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import java.util.Arrays;
import java.util.stream.Collectors;

import static de.bioforscher.units.UnitDictionary.*;

/**
 * Created by Christoph on 09.07.2016.
 */
public class ReactionsTest {

    private AutomatonGraph prepareGraph() {
        return BioGraphUtilities.castUndirectedGraphToBioGraph(GraphFactory.buildLinearGraph(1,
                new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0))));
    }

    @Test
    public void testEnzymeReaction() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma
        // brucei compared to aldolase from rabbit muscle and Staphylococcus
        // aureus.

        // Graph
        AutomatonGraph graph = prepareGraph();

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

        BiochemicalReaction reaction = new BiochemicalReaction(aldolase);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(fp, ReactantRole.DECREASING),
                new StoichiometricReactant(ga, ReactantRole.INCREASING),
                new StoichiometricReactant(gp, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        String header = graph.getNode(0).getConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getConcentrations().entrySet().stream()
                    .map(q -> String.valueOf(q.getValue().getValue().doubleValue()))
                    .collect(Collectors.joining(","));
            System.out.println(time + "," + csvLine);
        }
    }


    @Test
    public void testEquilibriumReaction() {

        // Graph
        AutomatonGraph graph = prepareGraph();

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

        EquilibriumReaction reaction = new EquilibriumReaction(
                Quantities.getQuantity(5, PER_SECOND),
                Quantities.getQuantity(10, PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(speciesA, ReactantRole.DECREASING),
                new StoichiometricReactant(speciesB, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        String header = graph.getNode(0).getConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getConcentrations().entrySet().stream()
                    .map(q -> String.valueOf(q.getValue().getValue().doubleValue()))
                    .collect(Collectors.joining(","));
            System.out.println(time + "," + csvLine);
        }

    }

    @Test
    public void testNthOrderReaction() {

        // Graph
        AutomatonGraph graph = prepareGraph();

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

        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.07, UnitDictionary.PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dpo, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        String header = graph.getNode(0).getConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getConcentrations().entrySet().stream()
                    .map(q -> String.valueOf(q.getValue().getValue().doubleValue()))
                    .collect(Collectors.joining(","));
            System.out.println(time + "," + csvLine);
        }

    }

}