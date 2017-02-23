package de.bioforscher.simulation.modules.model.updates;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioNode;

public interface UpdateBehavior {

    void updateGraph(AutomatonGraph graph);

    PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity);

}
