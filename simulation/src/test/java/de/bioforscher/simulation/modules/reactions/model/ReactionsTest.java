package de.bioforscher.simulation.modules.reactions.model;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.simulation.util.EnvironmentFactory;
import de.bioforscher.units.UnitDictionary;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import java.util.Arrays;

/**
 * Created by Christoph on 09.07.2016.
 */
public class ReactionsTest {

    @Test
    public void testReactionsModule() {

        // Graph
        AutomatonGraph graph = BioGraphUtilities.castUndirectedGraphToBioGraph(
                GraphFactory.buildLinearGraph(1, new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0))));

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
                new StoichiometricReactant(dpo, ReactantRole.DECREASING),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));

        Reactions reactions = new Reactions();
        reactions.getReactions().add(reaction);

        reactions.applyTo(graph);

    }

}