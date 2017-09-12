package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureSelector;
import de.bioforscher.singa.chemistry.parser.plip.InteractionContainer;
import de.bioforscher.singa.chemistry.parser.plip.PlipParser;
import de.bioforscher.singa.chemistry.parser.plip.PlipShellGenerator;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.mathematics.algorithms.graphs.NeighbourhoodExtractor;
import de.bioforscher.singa.mathematics.graphs.model.GenericGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import javafx.application.Application;
import javafx.scene.paint.Color;

import java.util.List;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author fk
 */
public class GraphViewerPlayground {

    public static void main(String[] args) {

        Structure structure = StructureParser.online()
                .pdbIdentifier("1c0a")
                .parse();

        Chain chain = structure.getFirstChain();

        LeafSubstructure<?, ?> reference = StructureSelector.selectFrom(chain)
                .atomContainer(831)
                .selectAtomContainer();

        InteractionContainer interInteractions = PlipParser.parse("1c0a", getResourceAsStream("1c0a.xml"));
        InteractionContainer ligandInteractions = PlipParser.parse("1c0a", getResourceAsStream("1c0a_ligand.xml"));

        PlipShellGenerator plipShellGenerator = PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interInteractions, ligandInteractions);

        GenericGraph<LeafSubstructure<?, ?>> graph = plipShellGenerator.getGraph();
        GenericNode<LeafSubstructure<?, ?>> referenceNode = graph.getNodeWithContent(reference).get();

        GenericGraph<LeafSubstructure<?, ?>> subgraph = NeighbourhoodExtractor.extractNeighborhood(graph, referenceNode, 3);
        List<GenericNode<LeafSubstructure<?, ?>>> firstShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 1);
        List<GenericNode<LeafSubstructure<?, ?>>> secondShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 2);
        List<GenericNode<LeafSubstructure<?, ?>>> thirdShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 3);

        GraphDisplayApplication.graph = subgraph;
        LeafShellRenderer renderer = new LeafShellRenderer();
        GraphDisplayApplication.renderer = renderer;

        renderer.setRenderBefore((currentGraph) -> {
            renderer.getGraphicsContext().setStroke(Color.DARKBLUE);
            for (GenericNode<LeafSubstructure<?, ?>> shellNode : firstShell) {
                renderer.circlePoint(currentGraph.getNode(shellNode.getIdentifier()).getPosition(), renderer.getRenderingOptions().getNodeDiameter() + 2);
            }
            renderer.getGraphicsContext().setStroke(Color.RED);
            for (GenericNode<LeafSubstructure<?, ?>> shellNode : secondShell) {
                renderer.circlePoint(currentGraph.getNode(shellNode.getIdentifier()).getPosition(), renderer.getRenderingOptions().getNodeDiameter() + 2);
            }
            renderer.getGraphicsContext().setStroke(Color.YELLOW);
            for (GenericNode<LeafSubstructure<?, ?>> shellNode : thirdShell) {
                renderer.circlePoint(currentGraph.getNode(shellNode.getIdentifier()).getPosition(), renderer.getRenderingOptions().getNodeDiameter() + 2);
            }
            return null;
        });

        Application.launch(GraphDisplayApplication.class);
    }


}
