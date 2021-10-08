package bio.singa.javafx.renderer.graphs;


import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.mathematics.algorithms.graphs.NeighbourhoodExtractor;
import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureSelector;
import bio.singa.structure.io.plip.InteractionContainer;
import bio.singa.structure.io.plip.PlipParser;
import bio.singa.structure.io.plip.PlipShellGenerator;
import javafx.application.Application;

import java.util.List;

import static bio.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author fk
 */
public class GraphViewerPlayground {

    public static void main(String[] args) {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .parse();

        Chain chain = structure.getFirstChain();

        LeafSubstructure reference = StructureSelector.selectFrom(chain)
                .atomContainer(831)
                .selectAtomContainer();

        InteractionContainer interInteractions = PlipParser.parse("1c0a", getResourceAsStream("1c0a.xml"));
        InteractionContainer ligandInteractions = PlipParser.parse("1c0a", getResourceAsStream("1c0a_ligand.xml"));

        PlipShellGenerator plipShellGenerator = PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interInteractions, ligandInteractions);

        GenericGraph<LeafSubstructure> graph = plipShellGenerator.getGraph();
        GenericNode<LeafSubstructure> referenceNode = graph.getNodeWithContent(reference).get();

        GenericGraph<LeafSubstructure> subgraph = NeighbourhoodExtractor.extractNeighborhood(graph, referenceNode, 3);
        List<GenericNode<LeafSubstructure>> firstShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 1);
        List<GenericNode<LeafSubstructure>> secondShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 2);
        List<GenericNode<LeafSubstructure>> thirdShell = NeighbourhoodExtractor.extractShell(graph, referenceNode, 3);
        String originalSmiles = "CC(=O)OC1=CC=CC=C1C(=O)O";
        MoleculeGraph moleculeGraph = SmilesParser.parse(originalSmiles);

        GenericGraph<Integer> testGraph = new GenericGraph<>();
        testGraph.addNode(1);
        testGraph.addNode(2);
        testGraph.addNode(3);
        testGraph.addNode(4);
        testGraph.addNode(5);
        testGraph.addNode(6);
        testGraph.addEdgeBetween(1, 2);
        testGraph.addEdgeBetween(1, 5);
        testGraph.addEdgeBetween(2, 5);
        testGraph.addEdgeBetween(2, 3);
        testGraph.addEdgeBetween(3, 4);
        testGraph.addEdgeBetween(4, 5);
        testGraph.addEdgeBetween(6, 4);



        GraphDisplayApplication.graph = moleculeGraph;
        GraphRenderer renderer = new GraphRenderer();
        GraphRenderOptions<?> objectGraphRenderOptions = new GraphRenderOptions<>();
        objectGraphRenderOptions.setDisplayText(true);
        renderer.setRenderingOptions(objectGraphRenderOptions);
        GraphDisplayApplication.renderer = renderer;

        Application.launch(GraphDisplayApplication.class);
    }


}
