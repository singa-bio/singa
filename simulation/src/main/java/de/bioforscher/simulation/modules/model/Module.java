package de.bioforscher.simulation.modules.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.AutomatonGraph;

import java.util.Set;

/**
 * Created by Christoph on 06.07.2016.
 */
public interface Module {

    void applyTo(AutomatonGraph graph);

    Set<ChemicalEntity> collectAllReferencesEntities();

}
