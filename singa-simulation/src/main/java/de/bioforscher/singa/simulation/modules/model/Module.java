package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;

import java.util.Set;

/**
 * @author cl
 */
public interface Module {

    void applyTo(AutomatonGraph graph);


    Set<ChemicalEntity<?>> collectAllReferencedEntities();

}
