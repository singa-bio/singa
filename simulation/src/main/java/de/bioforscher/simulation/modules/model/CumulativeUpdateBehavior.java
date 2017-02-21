package de.bioforscher.simulation.modules.model;

import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This {@link UpdateBehavior} calculates all updates first and than applies them. his behavior should by applied to
 * modules that require neighbor nodes to calculate their next state (e.g. Diffusion).
 */
public interface CumulativeUpdateBehavior extends UpdateBehavior {

    /**
     * Calculates all Updates and applies them afterwards.
     *
     * @param graph
     */
    @Override
    default void updateGraph(AutomatonGraph graph) {
        Map<BioNode, Set<PotentialUpdate>> potentialUpdates = new HashMap<>();
        // collect Updates
        graph.getNodes().forEach(node -> potentialUpdates.put(node, calculateNode(node)));
        // applyTo updates
        for (BioNode node : graph.getNodes()) {
            for (PotentialUpdate update : potentialUpdates.get(node)) {
                node.setConcentration(update.getEntity(), update.getQuantity());
            }
        }
    }

    /**
     * Calculates the {@link PotentialUpdate}s for all species in the given node and returns them.
     *
     * @param node
     * @return
     */
    default Set<PotentialUpdate> calculateNode(BioNode node) {
        return node.getConcentrations().keySet().stream()
                .map(entity -> calculateUpdate(node, entity))
                .collect(Collectors.toSet());
    }


}