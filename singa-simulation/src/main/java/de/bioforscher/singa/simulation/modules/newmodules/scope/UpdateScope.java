package de.bioforscher.singa.simulation.modules.newmodules.scope;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.LocalError;
import de.bioforscher.singa.simulation.modules.model.Updatable;

import java.util.Collection;

/**
 * decides on half concentration storage behaviour
 * @author cl
 */
public interface UpdateScope {

    void processAllUpdatables(Collection<Updatable> updatables);

    LocalError processUpdatable(Updatable updatable);

    void clearPotentialDeltas(Updatable updatable);

    ConcentrationContainer getHalfStepConcentration(Updatable updatable);

}
