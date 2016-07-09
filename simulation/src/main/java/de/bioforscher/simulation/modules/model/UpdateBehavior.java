package de.bioforscher.simulation.modules.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;

public interface UpdateBehavior {

    void updateGraph(AutomatonGraph graph);

    PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity);

}
