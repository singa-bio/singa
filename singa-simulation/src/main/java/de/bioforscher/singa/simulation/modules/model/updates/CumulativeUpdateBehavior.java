package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;

import java.util.List;
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
        // collect updates
        List<PotentialUpdate> updates = graph.getNodes().stream()
                .flatMap(node -> calculateUpdates(node).stream())
                .collect(Collectors.toList());
        // apply updates
        updates.forEach(PotentialUpdate::apply);
    }

}