package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This {@link UpdateBehavior} calculates all updates first and than applies them. his behavior should by applied to
 * panes that require neighbor nodes to calculate their next state (e.g. Diffusion).
 */
public interface CumulativeUpdateBehavior extends UpdateBehavior {

    /**
     * Calculates all updates and applies them afterwards.
     *
     * @param graph The graph.
     */
    @Override
    default void updateGraph(AutomatonGraph graph) {
        Map<BioNode, Set<PotentialUpdate>> potentialUpdates = new HashMap<>();
        // collect Updatesd
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
     * @param node The node.
     * @return A set of potential updates.
     */
    default Set<PotentialUpdate> calculateNode(BioNode node) {
        return node.getAllReferencedEntities().stream()
                .map(entity -> calculateUpdate(node, entity))
                .collect(Collectors.toSet());
    }


}