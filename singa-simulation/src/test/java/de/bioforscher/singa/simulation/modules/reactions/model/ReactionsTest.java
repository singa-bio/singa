package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioEdge;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.reactions.implementations.BiochemicalReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.singa.units.UnitProvider;
import de.bioforscher.singa.units.features.model.FeatureOrigin;
import de.bioforscher.singa.units.parameters.EnvironmentalParameterExamples;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import java.util.Arrays;
import java.util.stream.Collectors;

import static de.bioforscher.singa.units.UnitProvider.*;

/**
 * @author cl
 */
public class ReactionsTest {

    private AutomatonGraph prepareGraph() {
        return AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildLinearGraph(1,
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
        Species fp = ChEBIParserService.parse("CHEBI:18105");
        Species gp = ChEBIParserService.parse("CHEBI:16108");
        Species ga = ChEBIParserService.parse("CHEBI:17378");

        // Enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .assignFeature(new MolarMass(82142, FeatureOrigin.MANUALLY_ANNOTATED))
                .addSubstrate(fp)
                .michaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE))
                .turnoverNumber(Quantities.getQuantity(76, PER_MINUTE))
                .build();

        // set concentrations
        for (BioNode node : graph.getNodes()) {
            node.setConcentration(fp, 0.1);
            node.setConcentration(aldolase, 0.2);
            node.setConcentration(ga, 0);
            node.setConcentration(gp, 0);
        }

        // Environment
        EnvironmentalParameterExamples.createEnzymeReactionTestEnvironment();

        BiochemicalReaction reaction = new BiochemicalReaction(aldolase);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(fp, ReactantRole.DECREASING),
                new StoichiometricReactant(ga, ReactantRole.INCREASING),
                new StoichiometricReactant(gp, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        String header = graph.getNode(0).getAllConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getAllConcentrations().entrySet().stream()
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
                .build();

        Species speciesB = new Species.Builder("CHEBI:00002")
                .name("B")
                .build();

        for (BioNode node : graph.getNodes()) {
            node.setConcentration(speciesA, 1.0);
            node.setConcentration(speciesB, 0.0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(speciesA, 1);
            edge.addPermeability(speciesB, 1);
        }

        // Environment
        EnvironmentalParameterExamples.createFirstOrderReactionTestEnvironment();

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

        String header = graph.getNode(0).getAllConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getAllConcentrations().entrySet().stream()
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
        Species dpo = ChEBIParserService.parse("CHEBI:29802");
        // nitrogen dioxide
        Species ndo = ChEBIParserService.parse("CHEBI:33101");
        // dioxigen
        Species oxygen = ChEBIParserService.parse("CHEBI:15379");

        for (BioNode node : graph.getNodes()) {
            node.setConcentration(dpo, 0.020);
            node.setConcentration(ndo, 0);
            node.setConcentration(oxygen, 0);
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(dpo, 1);
            edge.addPermeability(ndo, 1);
            edge.addPermeability(oxygen, 1);
        }

        // Environment
        EnvironmentalParameterExamples.createFirstOrderReactionTestEnvironment();

        NthOrderReaction reaction = new NthOrderReaction(Quantities.getQuantity(0.07, UnitProvider.PER_SECOND));
        reaction.setElementary(true);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dpo, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        String header = graph.getNode(0).getAllConcentrations().keySet().stream()
                .map(ChemicalEntity::getName)
                .collect(Collectors.joining(","));
        System.out.println("time," + header);

        for (int time = 0; time < 10000; time++) {
            reactions.applyTo(graph);
            String csvLine = graph.getNode(0).getAllConcentrations().entrySet().stream()
                    .map(q -> String.valueOf(q.getValue().getValue().doubleValue()))
                    .collect(Collectors.joining(","));
            System.out.println(time + "," + csvLine);
        }

    }

}