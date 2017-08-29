package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.mathematics.graphs.model.GenericGraph;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PlipShellGenerator {

    private final Chain chain;
    private final LeafSubstructure<?, ?> reference;
    private final InteractionContainer interChainInteractions;
    private final InteractionContainer referenceInteractions;
    private Map<InteractionShell, Set<LeafSubstructure<?, ?>>> shells;

    private GenericGraph<LeafSubstructure<?, ?>> graph;

    private PlipShellGenerator(Chain chain, LeafSubstructure<?, ?> reference, InteractionContainer interChainInteractions,
                               InteractionContainer referenceInteractions) {

//        if (!(reference instanceof AtomContainer && reference.getFamily() instanceof LigandFamily)) {
//            throw new IllegalArgumentException("reference must be a valid ligand");
//        }

        this.chain = chain;
        this.reference = reference;
        this.interChainInteractions = interChainInteractions;
        this.referenceInteractions = referenceInteractions;
        this.shells = new TreeMap<>();
        this.graph = new GenericGraph<>();
        computeShells();
        generateInterInteractionGraph();
    }

    public static PlipShellGenerator getInteractionShellsForLigand(Chain chain, LeafSubstructure<?, ?> reference, InteractionContainer interChainInteractions, InteractionContainer referenceInteractions) {
        return new PlipShellGenerator(chain, reference, interChainInteractions, referenceInteractions);
    }

    public Map<InteractionShell, Set<LeafSubstructure<?, ?>>> getShells() {
        return shells;
    }

    private void computeShells() {
        // compute first shell (directly interacting with reference)
        Set<LeafSubstructure<?, ?>> firstShell = referenceInteractions.getInteractions().stream()
                .filter(interaction -> interaction.getTarget().equals(reference.getIdentifier()))
                .map(Interaction::getSource)
                .map(leafIdentifier -> chain.getLeafSubstructures().stream()
                        .filter(leafSubstructure -> leafSubstructure.getIdentifier().equals(leafIdentifier))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        shells.put(InteractionShell.FIRST, firstShell);
        // add reference and its interactions to graph
        graph.addNode(reference);
        for (LeafSubstructure<?, ?> leafSubstructure : firstShell) {
            graph.addNode(leafSubstructure);
            graph.addEdgeBetween(reference, leafSubstructure);
        }


    }

    private void generateInterInteractionGraph() {
        // generate interaction graph from inter chain interactions
        for (Interaction interaction : interChainInteractions.getInteractions()) {
            LeafSubstructure<?, ?> source = chain.getLeafSubstructure(interaction.getSource());
            LeafSubstructure<?, ?> target = chain.getLeafSubstructure(interaction.getTarget());
            graph.addEdgeBetween(source, target);
        }
    }

    private void getNextShell(InteractionShell previousShell) {
        Set<LeafSubstructure<?, ?>> previousLeafSubstructures = shells.get(previousShell);
        for (LeafSubstructure<?, ?> previousLeafSubstructure : previousLeafSubstructures) {

        }
    }

    public GenericGraph<LeafSubstructure<?, ?>> getGraph() {
        return graph;
    }

    public enum InteractionShell {
        FIRST, SECOND, THIRD
    }
}
