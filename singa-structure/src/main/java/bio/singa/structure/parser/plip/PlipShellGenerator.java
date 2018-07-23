package bio.singa.structure.parser.plip;

import bio.singa.mathematics.algorithms.graphs.NeighbourhoodExtractor;
import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates the shell of residues in indirect contact with the ligand utilizing PLIP interaction data. According to:
 * <pre>
 * Brodkin, HR, DeLateur, NA, Somarowthu, S, Mills, CL, Novak, WR, Beuning, PJ, Ringe, D, Ondrechen, MJ (2015).
 * Prediction of distal residue participation in enzyme catalysis.
 * Protein Sci., 24, 5:762-78.
 * </pre>
 *
 * @author fk
 */
public class PlipShellGenerator {

    private final Chain chain;
    private final LeafSubstructure reference;
    private final InteractionContainer interChainInteractions;
    private final InteractionContainer referenceInteractions;
    private final Map<InteractionShell, List<LeafSubstructure<?>>> shells;

    private final GenericGraph<LeafSubstructure<?>> graph;

    private PlipShellGenerator(Chain chain, LeafSubstructure reference, InteractionContainer interChainInteractions,
                               InteractionContainer referenceInteractions) {
        this.chain = chain;
        this.reference = reference;
        this.interChainInteractions = interChainInteractions;
        this.referenceInteractions = referenceInteractions;
        shells = new TreeMap<>();
        graph = new GenericGraph<>();
        generateInteractionGraph();
        computeShells();
    }

    public static PlipShellGenerator getInteractionShellsForLigand(Chain chain, LeafSubstructure<?> reference,
                                                                   InteractionContainer interChainInteractions,
                                                                   InteractionContainer referenceInteractions) {
        return new PlipShellGenerator(chain, reference, interChainInteractions, referenceInteractions);
    }

    private void computeShells() {
        GenericNode<LeafSubstructure<?>> referenceNode = graph.getNodeWithContent(reference)
                .orElseThrow(() -> new IllegalArgumentException("No such reference node in interaction graph."));
        for (InteractionShell interactionShell : InteractionShell.values()) {
            shells.put(interactionShell, interactionShell.from(graph, referenceNode));
        }
    }

    public Map<InteractionShell, List<LeafSubstructure<?>>> getShells() {
        return shells;
    }

    private void generateInteractionGraph() {
        // compute first shell (directly interacting with reference)
        Set<LeafSubstructure> firstShell = referenceInteractions.getInteractions().stream()
                .filter(interaction -> interaction.getTarget().equals(reference.getIdentifier()))
                .map(Interaction::getSource)
                .map(leafIdentifier -> chain.getAllLeafSubstructures().stream()
                        .filter(leafSubstructure -> leafSubstructure.getIdentifier().equals(leafIdentifier))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        // add reference and its interactions to graph
        graph.addNode(reference);
        for (LeafSubstructure leafSubstructure : firstShell) {
            graph.addNode(leafSubstructure);
            graph.addEdgeBetween(reference, leafSubstructure);
        }
        // generate interaction graph from inter chain interactions
        for (Interaction interaction : interChainInteractions.getInteractions()) {
            Optional<LeafSubstructure<?>> source = chain.getLeafSubstructure(interaction.getSource());
            Optional<LeafSubstructure<?>> target = chain.getLeafSubstructure(interaction.getTarget());
            if (source.isPresent() && target.isPresent()) {
                graph.addEdgeBetween(source.get(), target.get());
            }
        }
    }

    public GenericGraph<LeafSubstructure<?>> getGraph() {
        return graph;
    }

    public enum InteractionShell {

        FIRST, SECOND, THIRD;

        public List<LeafSubstructure<?>> from(GenericGraph<LeafSubstructure<?>> graph,
                                              GenericNode<LeafSubstructure<?>> referenceNode) {
            return NeighbourhoodExtractor.extractShell(graph, referenceNode, ordinal() + 1).stream()
                    .map(GenericNode::getContent)
                    .collect(Collectors.toList());
        }
    }
}
