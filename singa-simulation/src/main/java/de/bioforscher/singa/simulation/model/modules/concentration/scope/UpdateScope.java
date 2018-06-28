package de.bioforscher.singa.simulation.model.modules.concentration.scope;

import de.bioforscher.singa.simulation.model.modules.concentration.LocalError;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Updatable;

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
