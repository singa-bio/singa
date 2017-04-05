package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

public interface UpdateBehavior {

    void updateGraph(AutomatonGraph graph);

    PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity);

}
