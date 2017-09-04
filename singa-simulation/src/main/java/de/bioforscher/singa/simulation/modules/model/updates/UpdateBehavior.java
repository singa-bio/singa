package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import java.util.List;

public interface UpdateBehavior {

    void updateGraph(AutomatonGraph graph);

    List<PotentialUpdate> calculateUpdates(BioNode node);

}
